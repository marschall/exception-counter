module com.github.marschall.exceptioncounter {

  exports com.github.marschall.excount;

  requires java.management;
  requires static jdk.attach;

}