package net.cublix.jdbc.query.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.cublix.jdbc.query.SyncQueryRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static net.cublix.jdbc.util.PrepareStatementUtil.setupParameters;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SyncQueryRunnerImpl implements SyncQueryRunner {

  HikariDataSource dataSource;

  @Override
  public ResultSet query(String query, List<Object> args) {
    try (var statement = dataSource.getConnection().prepareStatement(query)) {
      setupParameters(statement, args);
      return statement.executeQuery();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int update(String query, List<Object> args) {
    try (var statement = dataSource.getConnection().prepareStatement(query)) {
      setupParameters(statement, args);
      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
