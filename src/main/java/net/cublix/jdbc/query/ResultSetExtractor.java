package net.cublix.jdbc.query;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetExtractor<T> {

  T exctract(ResultSet rs);

}
