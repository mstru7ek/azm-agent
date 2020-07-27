package com.mstruzek.azm;

import org.objectweb.asm.*;

import static java.lang.String.format;
import static org.objectweb.asm.Opcodes.*;

public class AppendLoggerVisitor extends ClassVisitor {

  private boolean isLoggerPresent = false;
  private String ownerClassName = null;
  private String className;

  public AppendLoggerVisitor(ClassWriter cw, String className) {
    super(ASM5, cw);
    this.className = className;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    ownerClassName = name;
    if (!className.equals(ownerClassName)) {
      throw new IllegalStateException("Not expected class name");
    }
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
    if ((ACC_PRIVATE | ACC_STATIC | ACC_FINAL) == access && "logger".equals(name)) {
      isLoggerPresent = true;
    }
    return super.visitField(access, name, descriptor, signature, value);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    if (ACC_STATIC == access && name.equals("<clinit>") && isLoggerPresent && ownerClassName != null) {
      MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
      methodVisitor.visitCode();
      methodVisitor.visitLdcInsn(Type.getType(format("L%s;",ownerClassName)));
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
      methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/logging/Logger", "getLogger", "(Ljava/lang/String;)Ljava/util/logging/Logger;", false);
      methodVisitor.visitFieldInsn(PUTSTATIC, ownerClassName, "logger", "Ljava/util/logging/Logger;");
      methodVisitor.visitInsn(RETURN);
      methodVisitor.visitMaxs(1, 0);
      methodVisitor.visitEnd();
      return null;
    }
    return super.visitMethod(access, name, descriptor, signature, exceptions);
  }
}
