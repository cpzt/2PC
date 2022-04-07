package com.github.cpzt.common;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Log {
  private static final ConcurrentLinkedQueue<String> log = new ConcurrentLinkedQueue<>();

  public void write(String msg) {
    System.out.println(msg);
    log.add(msg);
  }

  public String first() {
    return log.poll();
  }
}
