package net.cublix.jdbc.repository;

import net.cublix.jdbc.query.ResultSetExtractor;

public interface RepositoryManager {

  <T> T createRepository(Class<T> repoClass);

  <T> void registerAdapter(Class<T> typeClass, ArgumentAdapter<T> adapter);

  <T> void registerExtractor(Class<T> typeClass, ResultSetExtractor<T> adapter);

}
