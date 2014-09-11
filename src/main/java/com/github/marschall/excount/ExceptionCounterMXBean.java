package com.github.marschall.excount;

public interface ExceptionCounterMXBean {

  int getCount();

  int clearAndGetCount();

}
