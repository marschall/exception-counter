package com.github.marschall.excount;

import java.io.IOException;
import java.io.PrintStream;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * Attach to another process and launch an exception counter agent in it.
 */
public class ExceptionCounterAttacher {

  public static void main(String[] args) {
    // https://github.com/giltene/jHiccup
    ExceptionCounterConfiguration configuration;
    try {
      configuration = new ExceptionCounterConfiguration(args);
    } catch (ConfigurationException e) {
      System.err.println(e.getMessage());
      usage(System.err);
      System.exit(1);
      return;
    }

    try {
      VirtualMachine vm = VirtualMachine.attach(configuration.pidOfProcessToAttachTo);
      vm.loadAgentPath(configuration.agentPath);
      vm.detach();
      System.out.println("successfully attached");
      System.exit(0);
    } catch (AttachNotSupportedException | AgentLoadException | AgentInitializationException | IOException  e) {
      System.err.println("failed to attach");
      e.printStackTrace(System.err);
      System.exit(0);
    }
  }

  private static void usage(PrintStream s) {
    String validArgs =
        "\"[-p pidOfProcessToAttachTo] [-a agentPath]\"\n";

    s.println("valid arguments = " + validArgs);

    s.println(
        " [-h]                        help\n"
        + " [-p pidOfProcessToAttachTo] Attach to the process with given pid and inject exception counter as an agent\n"
        + " [-a agentPath] Absolute patch to the exception counter agent binary\n");

  }

  static final class ExceptionCounterConfiguration {

    String pidOfProcessToAttachTo;
    String agentPath;

    public ExceptionCounterConfiguration(String[] args) throws ConfigurationException {
      for (int i = 0; i < args.length; ++i) {
        if (args[i].equals("-p")) {
          if (args.length >= i) {
            pidOfProcessToAttachTo = args[++i];
          } else {
            throw new ConfigurationException("Missing value for -p");
          }
        } else if (args[i].equals("-a")) {
          if (args.length >= i) {
            agentPath = args[++i];
          } else {
            throw new ConfigurationException("Missing value for -a");
          }
        } else {
          throw new ConfigurationException("Invalid args: " + args[i]);
        }
      }

      if (pidOfProcessToAttachTo == null) {
        throw new ConfigurationException("Missing argument -p");
      }
      if (agentPath == null) {
        throw new ConfigurationException("Missing argument -a");
      }

    }

  }

  static final class ConfigurationException extends Exception {

    ConfigurationException(String message) {
      super(message);
    }

  }

}
