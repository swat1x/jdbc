package net.cublix.jdbc.query;

import org.intellij.lang.annotations.Language;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncQueryRunner {

  default <T> CompletableFuture<T> query(@Language("sql") String query, ResultSetExtractor<T> extractor) {
    return query(query, extractor, new Object[]{});
  }

  default <T> CompletableFuture<T> query(@Language("sql") String query, ResultSetExtractor<T> extractor, Object... args) {
    return query(query, extractor, List.of(args));
  }

  default CompletableFuture<Integer> update(@Language("sql") String query) {
    return update(query, new Object[]{});
  }

  default CompletableFuture<Integer> update(@Language("sql") String query, Object... args) {
    return update(query, List.of(args));
  }

  <T> CompletableFuture<T> query(@Language("sql") String query, ResultSetExtractor<T> extractor, List<Object> args);

  CompletableFuture<Integer> update(@Language("sql") String query, List<Object> args);

}
