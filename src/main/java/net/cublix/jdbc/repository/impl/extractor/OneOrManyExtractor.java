package net.cublix.jdbc.repository.impl.extractor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.cublix.jdbc.query.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OneOrManyExtractor implements ResultSetExtractor {

  boolean isList;
  ResultSetExtractor extractor;

  @Override
  public Object exctract(ResultSet rs) {
    try {
      if (isList) {
        var list = new ArrayList<>();
        while (rs.next()) {
          list.add(extractor.exctract(rs));
        }
        return list;
      } else {
        if (rs.next()) return extractor.exctract(rs);
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
