package com.mstruzek.agent;

import com.mstruzek.azm.AppendLogTransformation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class TransformationAgent {

  public static void premain(String args, Instrumentation instrumentation) {

    instrumentation.addTransformer(new ClassFileTransformer() {
      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                              ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.startsWith(args)) {
          try {
            AppendLogTransformation transformation = new AppendLogTransformation();
            byte[] b = transformation.apply(loader, className, classfileBuffer);
            return b;
          } catch (Throwable e) {
            e.printStackTrace();
            return null;
          }
        }
        return null; // no transformation
      }
    });
  }
}
