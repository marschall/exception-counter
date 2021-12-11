Exception Counter JVM Agent [![Build Status](https://travis-ci.org/marschall/exception-counter.svg?branch=master)](https://travis-ci.org/marschall/exception-counter)
===========================

A very simple JMTI agent that counts how many exceptions have occurred in a JVM.

Also contains an MBean that can be used for monitoring.

<img src="https://raw.githubusercontent.com/marschall/exception-counter/master/src/doc/exception-count-2.png" width="558" height="350" alt="VisualVM"/>

How do I run it?
----------------

Adding `-agentpath:/path/to/agent` is the simplest way to do it. To read the values the JAR has to be in the classpath as well.

Can I attach at runtime?
------------------------

Attaching with `$JAVA_HOME/bin/java -cp $JAVA_HOME/lib/tools.jar:exception-counter.jar com.github.marschall.excount.ExceptionCounterAttacher -p pidOfProcessToAttachTo -a /path/to/agent` is the simplest way to do it. To read the values the JAR has to be in the classpath as well.

Why a JMTI agent and not bytecode instrumentation?
--------------------------------------------------

A JMTI agent has several advantages over bytecode instrumentation:

 - It also reports exceptions raised by the VM (eg. `NullPointerException` or `ArithmeticException`) or native code (eg. `IOException`).
 - It should have a much smaller overhead.
 - It less intrusive for things like debugging.
 - It is unaffected by class file format changes.

How long until the 32bit counter overflows?
-------------------------------------------

With 60 exceptions per second after a year.

Why also reporting caught exceptions?
-------------------------------------

If you run inside a framework or container you will likely have a top-level exception handler that catches everything. Therefore you're unlikely to have any uncaught exceptions.

Which exceptions are not reported?
----------------------------------

To quote from the [JMTI documentation](http://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#Exception)

> If an exception is set and cleared in a native method (and thus is never visible to Java programming language code), no exception event is generated. 

I'm seeing lots of exceptions but everything seems to work fine?
----------------------------------------------------------------

You may be using libraries or frameworks that rely on exceptions for control flow. In addition central parts of Java like class loading use exceptions for control flow.

What are the requirements of the C compiler?
--------------------------------------------

The compiler should support C11 Atomics and provide the stdatomic.h header.

I need more information about my exceptions, where and when they happen.
------------------------------------------------------------------------

You may want to look into licensing [Java Mission Control](http://www.oracle.com/technetwork/java/javaseproducts/mission-control/java-mission-control-1998576.html).

Should I run random JVM agents from people on the Internet?
-----------------------------------------------------------

You should most definitely not. You should review the source and build from the source.

