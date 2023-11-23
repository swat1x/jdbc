package net.cublix.jdbc.repository.impl.adapter;

import net.cublix.jdbc.repository.ArgumentAdapter;

public class FallbackArgumentAdapter implements ArgumentAdapter<Object> {

  @Override
  public String[] map(Object instance) {
    return new String[]{instance.toString()};
  }

}
