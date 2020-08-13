package com.mstruzek.azm.mapper;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class MapperClassFactory {

  private MapperDefinition mapperDefinition;

  public MapperClassFactory(MapperDefinition mapperDefinition) {
    this.mapperDefinition = mapperDefinition;
  }

  public byte[] buildClass() {
    String sourceClass = mapperDefinition.getOutputClassName().substring(mapperDefinition.getOutputClassName().lastIndexOf('/'));
    ClassWriter classWriter = new ClassWriter(0);
    classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, mapperDefinition.getOutputClassName(), null, "java/lang/Object", new String[]{mapperDefinition.getInputInterfaceName()});
    classWriter.visitSource(sourceClass.concat(".java"), null);

    {
      buildDefaultConstructor(classWriter);

      buildSingleTransferMethod(classWriter);
    }

    classWriter.visitEnd();
    return classWriter.toByteArray();
  }

  private void buildDefaultConstructor(ClassWriter classWriter) {
    MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    methodVisitor.visitCode();
    {
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitLineNumber(3, label0);
      methodVisitor.visitVarInsn(ALOAD, 0);
      methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      methodVisitor.visitInsn(RETURN);
      Label label1 = new Label();
      methodVisitor.visitLabel(label1);
      methodVisitor.visitLocalVariable("this", "L".concat(mapperDefinition.getOutputClassName()).concat(";"), null, label0, label1, 0);
      methodVisitor.visitMaxs(1,1);
    }
    methodVisitor.visitEnd();
  }

  private void buildSingleTransferMethod(ClassWriter classWriter) {
    String methodDescriptor = String.format("(%s)%s", asRef(mapperDefinition.getInputType().getName()), asRef(mapperDefinition.getOutputType().getName()));
    {
      MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, mapperDefinition.getSingleMethod().getName(), methodDescriptor, null, null);
      methodVisitor.visitCode();
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitLineNumber(6, label0);

      String inputTypeNameRef = asClass(mapperDefinition.getInputType().getName());
      String resultTypeNameRef = asClass(mapperDefinition.getOutputType().getName());

      methodVisitor.visitTypeInsn(NEW, resultTypeNameRef);
      methodVisitor.visitInsn(DUP);
      methodVisitor.visitMethodInsn(INVOKESPECIAL, resultTypeNameRef, "<init>", "()V", false);
      methodVisitor.visitVarInsn(ASTORE, 2);

      int fLine = 7;

      for (Map.Entry<Method, Method> transfer : mapperDefinition.getTransferTo().entrySet()) {
        Label fieldLabel = new Label();
        methodVisitor.visitLabel(fieldLabel);
        methodVisitor.visitLineNumber(fLine++, fieldLabel);

        buildFieldSection(methodVisitor, transfer.getKey(), transfer.getValue(), inputTypeNameRef, resultTypeNameRef);
      }

      //return
      Label label3 = new Label();
      methodVisitor.visitLabel(label3);
      methodVisitor.visitLineNumber(fLine, label3);
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitInsn(ARETURN);

      Label label4 = new Label();
      methodVisitor.visitLabel(label4);
      methodVisitor.visitLocalVariable("this", asRef(mapperDefinition.getOutputClassName()), null, label0, label4, 0);
      methodVisitor.visitLocalVariable("input", asRef(mapperDefinition.getInputType().getName()), null, label0, label4, 1);
      methodVisitor.visitLocalVariable("result", asRef(mapperDefinition.getOutputType().getName()), null, label0, label4, 2);

      methodVisitor.visitMaxs(2, 3);
      methodVisitor.visitEnd();
    }
  }

  private void buildFieldSection(MethodVisitor methodVisitor, Method getterMethod, Method setterMethod, String inputTypeNameRef, String resultTypeNameRef) {
    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, inputTypeNameRef, getterMethod.getName(), asGetter(getterMethod.getReturnType()), false);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, resultTypeNameRef, setterMethod.getName(), asSetter(setterMethod.getParameterTypes()[0]), false);
  }

  private static String asGetter(Class<?> returnType) {
    return String.format("()%s",asRef(returnType.getName()));
  }

  private static String asSetter(Class<?> parameterType) {
    return String.format("(%s)V",asRef(parameterType.getName()));
  }

  private static String asRef(String className) {
    return "L".concat(asClass(className)).concat(";");
  }

  private static String asClass(String className) {
    return className.replace(".", "/");
  }


}
