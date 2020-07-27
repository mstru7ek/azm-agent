package com.mstruzek.azm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AppendLogTransformation {

  public byte[] apply(ClassLoader loader, String className, byte[] classfileBuffer) throws IOException {

    ByteArrayInputStream bais = new ByteArrayInputStream(classfileBuffer);

    ClassWriter cw = new ClassWriter(0);
    ClassReader cr = new ClassReader(bais);
    AppendLoggerVisitor cp = new AppendLoggerVisitor(cw, className);

    cr.accept(cp, 0);

    return cw.toByteArray();
  }

}
