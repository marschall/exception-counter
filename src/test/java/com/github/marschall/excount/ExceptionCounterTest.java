package com.github.marschall.excount;

import static com.github.marschall.excount.ExceptionCounterMXBean.OBJECT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExceptionCounterTest {

  private ExceptionCounterMXBean counter;

  @BeforeAll
  static void register() {
    ExceptionCounter.register();
  }

  @AfterAll
  static void unregister() {
    ExceptionCounter.unregister();
  }

  @BeforeEach
  void setUp() throws JMException {
    ObjectName objectName = new ObjectName(OBJECT_NAME);
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    this.counter = JMX.newMXBeanProxy(server, objectName, ExceptionCounterMXBean.class);
  }

  @Test
  void getCount() {
    int baseCount = this.counter.getCount();
    incrementExceptionCountByThrow();
    assertEquals(baseCount + 1, this.counter.getCount(), "exception count");
    incrementExceptionCountByDivisionByZero();
    assertEquals(baseCount + 2, this.counter.getCount(), "exception count");
    incrementExceptionCountByNullPointer();
    assertEquals(baseCount + 3, this.counter.getCount(), "exception count");
  }

  @Test
  void clearAndGetCount() {
    this.counter.clearAndGetCount();
    incrementExceptionCountByThrow();
    assertEquals(1, this.counter.clearAndGetCount(), "exception count");
    incrementExceptionCountByDivisionByZero();
    assertEquals(1, this.counter.clearAndGetCount(), "exception count");
    incrementExceptionCountByNullPointer();
    assertEquals(1, this.counter.clearAndGetCount(), "exception count");
  }

  private static void incrementExceptionCountByNullPointer() {
    assertThrows(NullPointerException.class, () -> nullPointerException(null));
  }

  private static String nullPointerException(Object o) {
    return o.toString();
  }

  private static void incrementExceptionCountByThrow() {
    assertThrows(RuntimeException.class, ExceptionCounterTest::throwException);
  }

  private static void throwException() {
    throw new RuntimeException();
  }

  private static void incrementExceptionCountByDivisionByZero() {
    assertThrows(ArithmeticException.class, () -> divisionByZero(0));
  }

  private static int divisionByZero(int i) {
    return 1 / i;
  }

}
