package com.github.marschall.excount;


/**
 * Exposes the number of exceptions that happened.
 */
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
  
  /**
   * Object name of the exception counter.
   * 
   * @see ExceptionCounter#register()
   * @see ExceptionCounter#unregister()
   */
  String OBJECT_NAME = "com.github.marschall.excount:type=ExceptionCounter";

  /**
   * Returns the number of exceptions that happened.
   * 
   * @return the number of exceptions that happened
   */
  int getCount();

  /**
   * Returns the number of exceptions that happened and then sets the
   * number of exceptions that happened to {@code 0}.
   * 
   * @return the number of exceptions that happened
   */
  int clearAndGetCount();

}
