package net.cublix.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Database {

  HikariDataSource dataSource;

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
  }

  @Value(staticConstructor = "of")
  public static class Credentials {

    String username;
    String password;

  }

}
