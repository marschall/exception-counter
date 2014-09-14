package com.github.marschall.excount;


public interface ExceptionCounterMXBean {
  
//  static {
//    try {
//      OBJECT_NAME = new ObjectName("com.github.marschall.excount:type=ExceptionCounter");
//    } catch (MalformedObjectNameException e) {
//      throw new RuntimeException("invalid object name", e);
//    }
//  }
//  
//  ObjectName OBJECT_NAME;
  
  String OBJECT_NAME = "com.github.marschall.excount:type=ExceptionCounter";

  int getCount();

  int clearAndGetCount();

}
