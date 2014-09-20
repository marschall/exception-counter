package com.github.marschall.excount;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Implementation of {@link ExceptionCounterMXBean}.
 * 
 * <p>
 * An instance of this class can be registered as an MBean using
 * {@link #register()}.
 */
public final class ExceptionCounter implements ExceptionCounterMXBean {

  /**
   * Registers an instance under {@value #OBJECT_NAME} in the platform
   * MBean server.
   * <p>
   * Code that calls this should eventually call {@link #unregister()}
   * in order to avoid class loader leaks.
   * 
   * @see #unregister()
   */
  public static void register() {
    try {
      ObjectName objectName = new ObjectName(OBJECT_NAME);
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();

      ExceptionCounterMXBean mxBean = new ExceptionCounter();
      server.registerMBean(mxBean , objectName);
    } catch (JMException e) {
      throw new RuntimeException("could not register exception counter", e);
    }
  }

  /**
   * Unregisters the instance registered in {@link #register()}.
   * 
   * @see #register()
   */
  public static void unregister() {
    try {
      ObjectName objectName = new ObjectName(OBJECT_NAME);
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();

      server.unregisterMBean(objectName);
    } catch (JMException e) {
      throw new RuntimeException("could not unregister exception counter", e);
    }
  }

  @Override
  public native int getCount();

  @Override
  public native int clearAndGetCount();

}
