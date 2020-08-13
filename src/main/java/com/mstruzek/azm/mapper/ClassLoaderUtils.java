package com.mstruzek.azm.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ClassLoaderUtils {

  public static Class<?> defineClass(ClassLoader classLoader, String className, byte[] body) {
    Method defineClassMethod = findDefineClassMethod(classLoader);
    defineClassMethod.setAccessible(true);
    Class<?> definedClass = invoke(defineClassMethod, classLoader, className, body);
    defineClassMethod.setAccessible(false);
    return definedClass;
  }

  private static Class<?> invoke(Method defineClassMethod, ClassLoader classLoader, String className, byte[] body) {
    try {
      return (Class<?>) defineClassMethod.invoke(classLoader, className, body, 0, body.length);
    } catch (IllegalAccessException| InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static Method findDefineClassMethod(ClassLoader classLoader){
    return findMethodInInheritanceChain(classLoader.getClass(), "defineClass", new Class<?>[] {String.class, byte[].class, int.class, int.class});
  }

  private static Method findMethodInInheritanceChain(Class<?> target , String methodName, Class<?>[] arguments) {
    if (target == null) {
      throw new RuntimeException("Method not found : ". concat(methodName));
    }
    for (Method declaredMethod : target.getDeclaredMethods()) {
      if (declaredMethod.getName().equals(methodName) && Arrays.equals(declaredMethod.getParameterTypes(), arguments)) {
        return declaredMethod;
      }
    }
    return findMethodInInheritanceChain(target.getSuperclass(), methodName, arguments);
  }
}
