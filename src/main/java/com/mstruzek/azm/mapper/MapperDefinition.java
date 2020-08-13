package com.mstruzek.azm.mapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class MapperDefinition {

  private final Class<?> klass;
  private String inputInterfaceName;
  private String outputClassName;

  private Method singleMethod;
  private Class<?> inputType;
  private Class<?> outputType;

  private final Map<Method, Method> transferTo = new HashMap<>();

  private MapperDefinition(Class<?> klass) {
    this.klass = klass;
    introspectInterface();
  }

  public static MapperDefinition get(Class<?> klass) {
    return new MapperDefinition(klass);
  }

  private void introspectInterface() {
    if (klass.getDeclaredMethods().length == 0) {
      throw new IllegalStateException("Exactly one mapping method expected");
    }
    updateClassNames();
    findSingleMethod();
    findInputOutput();
    findGetterSetterMapping();
  }

  private void updateClassNames() {
    outputClassName = klass.getName().concat("_Mapper").replace(".", "/");
    inputInterfaceName = klass.getName().replace(".", "/");
  }

  private void findInputOutput() {
    inputType = singleMethod.getParameters()[0].getType();
    outputType = singleMethod.getReturnType();
  }

  private void findSingleMethod() {
    singleMethod = klass.getDeclaredMethods()[0];
    if (singleMethod.getParameterCount() != 1) {
      throw new IllegalStateException("Expected mapping method with single argument");
    }
  }

  private void findGetterSetterMapping() {
    for (Method outputMethod : outputType.getDeclaredMethods()) {
      if (outputMethod.getName().startsWith("set")) {
        Method inputMethod = findGetterMethod(inputType, outputMethod);
        transferTo.put(inputMethod, outputMethod);
      }
    }
  }

  private Method findGetterMethod(Class<?> inputType, Method outputMethod) {
    String name = "g".concat(outputMethod.getName().substring(1));
    for (Method inputMethod : inputType.getDeclaredMethods()) {
      if (inputMethod.getName().equals(name) &&
          inputMethod.getReturnType().equals(outputMethod.getParameterTypes()[0]))
        return inputMethod;
    }
    throw new RuntimeException("No matching getter/setter pair");
  }

  public String getOutputClassName() {
    return outputClassName;
  }

  public String getInputInterfaceName() {
    return inputInterfaceName;
  }

  public Method getSingleMethod() {
    return singleMethod;
  }

  public Class<?> getInputType() {
    return inputType;
  }

  public Class<?> getOutputType() {
    return outputType;
  }

  public Map<Method, Method> getTransferTo() {
    return transferTo;
  }
}
