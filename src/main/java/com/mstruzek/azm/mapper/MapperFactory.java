package com.mstruzek.azm.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MapperFactory {

  public static <T> T getInstance(Class<T> klass) {
    if (!klass.isInterface()) {
      throw new IllegalStateException("Interface type required");
    }
    return getInstance(klass, klass.getClassLoader());
  }

  private static <T> T getInstance(Class<T> klass, ClassLoader classLoader) {
    Class<?> aClass = loadClass(klass, classLoader);
    if (aClass == null) {
      aClass = buildMapperAdapter(klass, classLoader);
    }
    return createInstance(aClass);
  }

  private static <T> T createInstance(Class<?> aClass) {
    try {
      return (T) getNoArgConstructor(aClass).newInstance();
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Inaccessible default constructor", e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Invalid constructor definition", e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Constructor initialization failed", e);
    }
  }

  private static Constructor<?> getNoArgConstructor(Class<?> aClass) {
    try {
      return aClass.getConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Missing no-arg constructor", e);
    }
  }

  private static <T> Class<?> loadClass(Class<T> klass, ClassLoader classLoader) {
    try {
      return classLoader.loadClass(String.format("%s_Mapper", klass.getName()));
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static <T> Class<?> buildMapperAdapter(Class<T> klass, ClassLoader classLoader) {
    MapperDefinition mapperDefinition = MapperDefinition.get(klass);
    byte[] body = new MapperClassFactory(mapperDefinition).buildClass();
    return defineMapperClass(klass, classLoader, body);
  }

  private static <T> Class<?> defineMapperClass(Class<T> klass, ClassLoader classLoader, byte[] body) {
    return ClassLoaderUtils.defineClass(classLoader, klass.getName().concat("_Mapper"), body);
  }

}
