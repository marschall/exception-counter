package com.github.marschall.excount;

import static com.github.marschall.excount.ExceptionCounterMXBean.OBJECT_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("needs agent as jvm argument")
public class ExceptionCounterTest {
  
  private ExceptionCounterMXBean counter;
  
  @BeforeClass
  public static void register() {
    ExceptionCounter.register();
  }
  
  @AfterClass
  public static void unregister() {
    ExceptionCounter.unregister();
  }
  
  @Before
  public void setUp() throws JMException {
    ObjectName objectName = new ObjectName(OBJECT_NAME);
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    this.counter = JMX.newMXBeanProxy(server, objectName, ExceptionCounterMXBean.class);
  }

  @Test
  public void getCount() {
    int baseCount = counter.getCount();
    incrementExceptionCountByThrow();
    assertEquals("exception count", baseCount + 1, counter.getCount());
    incrementExceptionCountByDivisionByZero();
    assertEquals("exception count", baseCount + 2, counter.getCount());
    incrementExceptionCountByNullPointer();
    assertEquals("exception count", baseCount + 3, counter.getCount());
  }
  
  @Test
  public void clearAndGetCount() {
    counter.clearAndGetCount();
    incrementExceptionCountByThrow();
    assertEquals("exception count", 1, counter.clearAndGetCount());
    incrementExceptionCountByDivisionByZero();
    assertEquals("exception count", 1, counter.clearAndGetCount());
    incrementExceptionCountByNullPointer();
    assertEquals("exception count", 1, counter.clearAndGetCount());
  }
  
  private static void incrementExceptionCountByNullPointer() {
    try {
      nullPointerException(null);
      fail("exception not thrown");
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }
  
  private static String nullPointerException(Object o) {
    return o.toString();
  }
  
  private static void incrementExceptionCountByThrow() {
    try {
      throwException();
      fail("exception not thrown");
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }
  
  private static void throwException() {
    throw new RuntimeException();
  }
  
  private static void incrementExceptionCountByDivisionByZero() {
    try {
      divisionByZero(0);
      fail("exception not thrown");
    } catch (ArithmeticException e) {
      assertTrue(true);
    }
  }
  
  private static int divisionByZero(int i) {
    return 1 / i;
  }

}
