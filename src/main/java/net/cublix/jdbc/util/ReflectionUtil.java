package net.cublix.jdbc.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {

  private static Type popInUnsafe(Type parentType, int index) {
    var parameterizedType = (ParameterizedType) parentType;
    var types = parameterizedType.getActualTypeArguments();
    return types[index];
  }

  public static Type popIn(Type parentType, int index, String assertMessage) {
    assert parentType instanceof ParameterizedType : assertMessage;
    return popInUnsafe(parentType, index);
  }

  public static Type popIn(Type parentType, String assertMessage) {
    assert parentType instanceof ParameterizedType : assertMessage;
    return popInUnsafe(parentType, 0);
  }

  public static Type popIn(Type parentType, int index) {
    assert parentType instanceof ParameterizedType;
    return popInUnsafe(parentType, index);
  }

  public static Type popIn(Type parentType) {
    return popIn(parentType, 0);
  }

}
