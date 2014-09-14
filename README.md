Exception Counter JVM Agent [![Build Status](https://travis-ci.org/marschall/exception-counter.svg?branch=master)](https://travis-ci.org/marschall/exception-counter)
===========================

Attempt at building a JMTI agent. **NOT WORKING!**

### Why only a 32bit counter?
You can have 60 exceptions per second and run for a year.

Requires C11 Atomics (GCC 4.9+ or Clang 3.1+)

