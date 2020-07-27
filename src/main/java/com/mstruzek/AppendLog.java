package com.mstruzek;

import java.util.logging.Logger;

public class AppendLog {

  // lets replace all null loggers with Logger.getLogger(*.class.getName())
  private final static Logger logger = null;

  public static void main(String[] args) {

    logger.warning("Test log OK");

  }
}
