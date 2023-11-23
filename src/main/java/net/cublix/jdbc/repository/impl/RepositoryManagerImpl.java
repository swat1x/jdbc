package net.cublix.jdbc.repository.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import net.cublix.jdbc.Database;
import net.cublix.jdbc.query.ResultSetExtractor;
import net.cublix.jdbc.repository.ArgumentAdapter;
import net.cublix.jdbc.repository.annotation.CreateTable;
import net.cublix.jdbc.repository.annotation.Query;
import net.cublix.jdbc.repository.RepositoryManager;
import net.cublix.jdbc.repository.annotation.Update;
import net.cublix.jdbc.repository.impl.adapter.FallbackArgumentAdapter;
import net.cublix.jdbc.repository.impl.extractor.FallbackResultSetExtractor;
import net.cublix.jdbc.repository.impl.extractor.OneOrManyExtractor;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.cublix.jdbc.util.ReflectionUtil.popIn;

@Log4j2(topic = "RepositoryManager")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepositoryManagerImpl implements RepositoryManager {

  Database database;
  ArgumentAdapter<Object> fallbackAdapter;
  ResultSetExtractor<Object> fallbackExtractor;
  Map<Class<?>, ArgumentAdapter> adapterMap;
  Map<Class<?>, ResultSetExtractor> extractorMap;

  public RepositoryManagerImpl(Database database) {
    this.database = database;
    this.fallbackAdapter = new FallbackArgumentAdapter();
    this.fallbackExtractor = new FallbackResultSetExtractor();
    this.adapterMap = new HashMap<>();
    this.extractorMap = new HashMap<>();

    registerDefaults();
  }

  private void registerDefaults() {

  }

  @Override
  public <T> T createRepository(Class<T> repoClass) {
    var proxy = (T) Proxy.newProxyInstance(
            RepositoryManagerImpl.class.getClassLoader(),
            new java.lang.Class[]{repoClass},
            new RepositoryInvocationHandler()
    );
    invokeTableCreation(proxy);
    return proxy;
  }

  private void invokeTableCreation(Object proxy) {
    for (Method method : proxy.getClass().getMethods()) {
      if (method.getAnnotation(CreateTable.class) != null) {
        try {
          var update = method.getAnnotation(Update.class);
          database.sync().update(update.value());
          log.info("Table creation for repository '{}' executed!", proxy.getClass().getSimpleName());
        } catch (Exception e) {
          log.error("Can't invoke '{}' method in '{}' to create table",
                  method.getName(), proxy.getClass().getSimpleName());
        }
      }
    }
  }

  private Object[] parseArgs(Object instance) {
    for (var entry : adapterMap.entrySet()) {
      var instanceType = instance.getClass();
      var type = entry.getKey();
      var adapter = entry.getValue();
      if (type.equals(instanceType)) return adapter.map(instance);
    }
    return fallbackAdapter.map(instance);
  }

  @Override
  public <T> void registerAdapter(Class<T> typeClass, ArgumentAdapter<T> adapter) {
    adapterMap.put(typeClass, adapter);
    log.info("Adapter for {} registered!", typeClass.getSimpleName());
  }

  @Override
  public <T> void registerExtractor(Class<T> typeClass, ResultSetExtractor<T> adapter) {
    extractorMap.put(typeClass, adapter);
    log.info("Extractor for {} registered!", typeClass.getSimpleName());
  }

  private ResultSetExtractor getExtractor(Class<?> clazz) {
    return extractorMap.getOrDefault(clazz, fallbackExtractor);
  }

  private class RepositoryInvocationHandler implements InvocationHandler {

    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      var query = method.getAnnotation(Query.class);
      var update = method.getAnnotation(Update.class);
      if (query == null && update == null) {
        throw new IllegalStateException("Method " + method.getName() + " must have annotation @Query or @Update");
      }
      var isUpdate = update != null;
      var context = new ExecutionContextImpl();
      if (args != null) {
        for (Object arg : args) {
          context.add(parseArgs(arg));
        }
      }
//      var isVoid = method.getReturnType().equals(void.class);
      if (isUpdate) {
        Object result;
        if (update.async()) {
          result = database.async().update(update.value(), context.getParams());
        } else {
          result = database.sync().update(update.value(), context.getParams());
        }
        return result;
      } else {
        Type returnType = method.getGenericReturnType();
        var isAsync = returnType.getTypeName().startsWith("java.util.concurrent.CompletableFuture");
        if (isAsync) {
          returnType = popIn(returnType, "CompletableFuture must have 1 parameter type");
        }

        var isList = returnType.getTypeName().startsWith("java.util.List");
        if (isList) {
          returnType = popIn(returnType, "List<?> must have 1 parameter type");
        }

        var extractor = getExtractor(Class.forName(returnType.getTypeName()));
        extractor = new OneOrManyExtractor(isList, extractor);
        if (isAsync) {
          var future = new CompletableFuture<>();
          database.async().query(
                  query.value(),
                  extractor,
                  context.getParams()
          ).thenAccept(future::complete);
          return future;
        } else {
          return database.sync().query(
                  query.value(),
                  extractor,
                  context.getParams()
          );
        }
      }
    }
  }

}
