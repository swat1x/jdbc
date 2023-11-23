package net.cublix.jdbc.repository;

public interface ArgumentAdapter<T> {

  Object[] map(T instance);

}
