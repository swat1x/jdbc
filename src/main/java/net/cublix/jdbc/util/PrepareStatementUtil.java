package net.cublix.jdbc.util;

import lombok.experimental.UtilityClass;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@UtilityClass
public class PrepareStatementUtil {

  public static void setupParameters(PreparedStatement statement, Collection<Object> params) {
    try {
      int index = 1;
      for (Object param : params) {
        statement.setObject(index++, param);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
