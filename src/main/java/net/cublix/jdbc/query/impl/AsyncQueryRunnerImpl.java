package net.cublix.jdbc.query.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import net.cublix.jdbc.query.AsyncQueryRunner;
import net.cublix.jdbc.query.ResultSetExtractor;
import net.cublix.jdbc.query.SyncQueryRunner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Log4j2(topic = "AsyncQueryRunner")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AsyncQueryRunnerImpl implements AsyncQueryRunner {

  SyncQueryRunner queryRunner;
  ExecutorService executorService;

  @Override
  public <T> CompletableFuture<T> query(String query, ResultSetExtractor<T> extractor, List<Object> args) {
    var future = new CompletableFuture<T>();
    executorService.execute(() -> {
      try {
        var resultSet = queryRunner.query(query, args);
        future.complete(extractor.exctract(resultSet));
      } catch (Exception e) {
        log.error("Async query exception", e);
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<Integer> update(String query, List<Object> args) {
    var future = new CompletableFuture<Integer>();
    executorService.execute(() -> {
      try {
        var updatedLines = queryRunner.update(query, args);
        future.complete(updatedLines);
      } catch (Exception e) {
        log.error("Async update exception", e);
      }
    });
    return future;
  }

}
