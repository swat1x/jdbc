package net.cublix.jdbc.query;

import org.intellij.lang.annotations.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SyncQueryRunner {

  default <T> T query(@Language("sql") String query, ResultSetExtractor<T> extractor) {
    return query(query, extractor, new Object[]{});
  }

  default <T> T query(@Language("sql") String query, ResultSetExtractor<T> extractor, Object... args) {
    return query(query, extractor, List.of(args));
  }

  default ResultSet query(@Language("sql") String query, Object... args) {
    return query(query, List.of(args));
  }

  default int update(@Language("sql") String query) {
    return update(query, new Object[]{});
  }

  default int update(@Language("sql") String query, Object... args) {
    return update(query, List.of(args));
  }

  default <T> T query(@Language("sql") String query, ResultSetExtractor<T> extractor, List<Object> args) {
    return extractor.exctract(query(query, args));
  }

  ResultSet query(@Language("sql") String query, List<Object> args);

  int update(@Language("sql") String query, List<Object> args);

}
