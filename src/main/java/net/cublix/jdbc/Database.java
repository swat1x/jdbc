package net.cublix.jdbc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import net.cublix.jdbc.query.AsyncQueryRunner;
import net.cublix.jdbc.query.SyncQueryRunner;
import net.cublix.jdbc.query.impl.AsyncQueryRunnerImpl;
import net.cublix.jdbc.query.impl.SyncQueryRunnerImpl;
import net.cublix.jdbc.repository.RepositoryManager;
import net.cublix.jdbc.repository.impl.RepositoryManagerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Database {

  HikariDataSource dataSource;
  ExecutorService asyncExecutorService;
  RepositoryManager repositoryManager;

  @SneakyThrows
  public Database(
          String poolName,
          String driverName,
          String driverClassName,
          Credentials credentials,
          String host,
          String database
  ) {
    var config = new HikariConfig();
    config.setPoolName(poolName);

    // Credentials
    config.setUsername(credentials.getUsername());
    config.setPassword(credentials.getPassword());

    // Settings
    config.setDriverClassName(driverClassName);
    config.setJdbcUrl(("jdbc:%s://%s/%s" +
            "?useUnicode=yes" +
            "&useSSL=false" +
            "&characterEncoding=UTF-8" +
            "&allowMultiQueries=true" +
            "&autoReconnect=true" +
            "&jdbcCompliantTruncation=false").formatted(driverName, host, database));

    this.dataSource = new HikariDataSource(config);
    this.asyncExecutorService = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setUncaughtExceptionHandler((thread, throwable) -> {
                      log.error("Exception:", throwable);
                    })
                    .setNameFormat("Database-Thread-%d")
                    .build()
    );
    this.repositoryManager = new RepositoryManagerImpl(this);
  }

  @Value(staticConstructor = "of")
  public static class Credentials {

    String username;
    String password;

  }

  public AsyncQueryRunner async() {
    return new AsyncQueryRunnerImpl(sync(), asyncExecutorService);
  }

  public SyncQueryRunner sync() {
    return new SyncQueryRunnerImpl(dataSource);
  }

//  @SneakyThrows
//  public void query(@Language("sql") @NonNull String query, Consumer<ResultSet> resultSetConsumer, Object... args) {
//    try (var statement = getDataSource().getConnection().prepareStatement(query)) {
//      var formattedString = String.format(query, args);
//      for (int i = 1; i <= args.length; i++) {
//        statement.setObject(i, args[i - 1]);
//      }
//      var resultSet = statement.executeQuery();
//      resultSetConsumer.accept(resultSet);
//      resultSet.close();
//    }
//  }

}
