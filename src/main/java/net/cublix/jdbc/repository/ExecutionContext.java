package net.cublix.jdbc.repository;

import java.util.List;

public interface ExecutionContext {

  List<Object> getParams();

  void add(Object[] params);

}
