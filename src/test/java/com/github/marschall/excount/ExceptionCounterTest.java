package com.github.marschall.excount;

import static com.github.marschall.excount.ExceptionCounterMXBean.OBJECT_NAME;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExceptionCounterTest {

  static boolean initialized;

  private ExceptionCounterMXBean counter;

  static synchronized void loadLibrary() {
    if (initialized) {
      return;
    }
    String version = getVersion();
    String libraryName = "exception-counter-" + version;
    Runtime.getRuntime().loadLibrary(libraryName);
    initialized = true;
  }

  @BeforeClass
  public static void register() {
    loadLibrary();
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
    int baseCount = this.counter.getCount();
    incrementExceptionCountByThrow();
    assertEquals("exception count", baseCount + 1, this.counter.getCount());
    incrementExceptionCountByDivisionByZero();
    assertEquals("exception count", baseCount + 2, this.counter.getCount());
    incrementExceptionCountByNullPointer();
    assertEquals("exception count", baseCount + 3, this.counter.getCount());
  }

  @Test
  public void clearAndGetCount() {
    this.counter.clearAndGetCount();
    incrementExceptionCountByThrow();
    assertEquals("exception count", 1, this.counter.clearAndGetCount());
    incrementExceptionCountByDivisionByZero();
    assertEquals("exception count", 1, this.counter.clearAndGetCount());
    incrementExceptionCountByNullPointer();
    assertEquals("exception count", 1, this.counter.clearAndGetCount());
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

  private static String getVersion() {
    String fileName = "exception-counter.version";
    try (InputStream stream = ExceptionCounterTest.class.getClassLoader().getResourceAsStream(fileName)) {
      if (stream == null) {
        throw new AssertionError("could not load resource: " + fileName);
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
      stream.transferTo(bos);
      return new String(bos.toByteArray(), US_ASCII);
    } catch (IOException e) {
      throw new AssertionError("could not load file: " + fileName, e);
    }
  }

}
