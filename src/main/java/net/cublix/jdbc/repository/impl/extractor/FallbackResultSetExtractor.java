package net.cublix.jdbc.repository.impl.extractor;

import net.cublix.jdbc.query.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FallbackResultSetExtractor implements ResultSetExtractor<Object> {

  @Override
  public Object exctract(ResultSet rs) {
    try {
      return rs.getObject(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
