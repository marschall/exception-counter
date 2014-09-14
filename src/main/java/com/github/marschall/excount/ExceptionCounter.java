package com.github.marschall.excount;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class ExceptionCounter implements ExceptionCounterMXBean {
  
  static {
    System.loadLibrary("excount");
    
    try {
      ObjectName objectName = new ObjectName(OBJECT_NAME);
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      
      ExceptionCounterMXBean mxBean = new ExceptionCounter();
      server.registerMBean(mxBean , objectName);
    } catch (JMException e) {
      throw new RuntimeException("could not register exception counter", e);
    }
  }

  @Override
  public native int getCount();

  @Override
  public native int clearAndGetCount();

}
