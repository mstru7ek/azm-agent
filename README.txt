# Demo Application of ASM Instrumentation

# Result: Initialize all private static final loggers with appropriate Logger instance.

## Build Package
mvn clean package

## Run.

# Expect a null pointer exception. Log is not initialized.
java -jar target\azm-agent-1.0-SNAPSHOT.jar

# Expect to print out a log massage.
java -javaagent:target\azm-agent-1.0-SNAPSHOT.jar=com/mstruzek -jar target\azm-agent-1.0-SNAPSHOT.jar

# Filter out only selected package com/mstruzek


### Prepare ASM 

# Prepare ASM code base on bytecode. Then insert as replacement in MethodVisitor,FiledVisitor, ...
# -   cd target/classes/
java -classpath C:\Users\micha\.m2\repository\org\ow2\asm\asm-util\8.0.1\asm-util-8.0.1.jar;C:\Users\micha\.m2\repository\org\ow2\asm\asm\8.0.1\asm-8.0.1.jar;.  \
    org.objectweb.asm.util.ASMifier com.SimpleLog
