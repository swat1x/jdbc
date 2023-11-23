package net.cublix.jdbc.repository.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.cublix.jdbc.repository.ExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExecutionContextImpl implements ExecutionContext {

  List<Object> params;

  public ExecutionContextImpl() {
    this.params = new ArrayList<>();
  }

  @Override
  public void add(Object[] params) {
    Collections.addAll(this.params, params);
  }

}
