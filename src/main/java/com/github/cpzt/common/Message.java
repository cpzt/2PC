package com.github.cpzt.common;

import java.io.Serializable;

public class Message implements Serializable {

  public static final String START_2PC = "START_2PC";

  public static final String START_COMMIT = "START_COMMIT";

  public static final String VOTE = "VOTE";

  public static final String VOTE_ABORT = "VOTE_ABORT";

  public static final String COMMIT = "COMMIT";

  public static final String COMMIT_ABORT = "COMMIT_ABORT";

  private final String value;

  public Message(String value) {
    this.value = value;
  }

  public static Message of(String value) {
    return new Message(value);
  }

  public String getValue() {
    return this.value;
  }

}
