/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.telemetry.logs;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Telemetry;
import com.newrelic.telemetry.util.Utils;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** A Log instance represents a single entry in a log. */
public class Log implements Telemetry {
  private final long timestamp; // in epoch ms
  private final String message;
  private final Attributes attributes;
  private final String serviceName; // service.name <- goes in attributes
  private final String logType;
  private final String level;

  private Log(LogBuilder builder) {
    this.timestamp = builder.timestamp;
    this.message = builder.message;
    this.attributes = builder.attributes;
    this.serviceName = builder.serviceName;
    this.logType = builder.logType;
    this.level = builder.level;
  }

  /** The point in time (ms since UNIX epoch) that the log entry was created. */
  public long getTimestamp() {
    return timestamp;
  }

  /** The log line itself. */
  public String getMessage() {
    return message;
  }

  /** Additional attributes associated with the log entry. */
  public Attributes getAttributes() {
    return attributes;
  }

  /** The name of the service which produced this log entry. */
  public String getServiceName() {
    return serviceName;
  }

  /** The type of log entry. This can be helpful for querying your log entries in New Relic. */
  public String getLogType() {
    return logType;
  }

  /** The log level (eg. INFO, DEBUG, etc) for the log entry. */
  public String getLevel() {
    return level;
  }

  /** Create a builder for building a new log entry. */
  public static LogBuilder builder() {
    return new LogBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Log)) {
      return false;
    }

    Log log = (Log) o;

    if (timestamp != log.timestamp) {
      return false;
    }
    if (message != null ? !message.equals(log.message) : log.message != null) {
      return false;
    }
    if (attributes != null ? !attributes.equals(log.attributes) : log.attributes != null) {
      return false;
    }
    if (serviceName != null ? !serviceName.equals(log.serviceName) : log.serviceName != null) {
      return false;
    }
    if (logType != null ? !logType.equals(log.logType) : log.logType != null) {
      return false;
    }
    return level != null ? level.equals(log.level) : log.level == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (timestamp ^ (timestamp >>> 32));
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
    result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
    result = 31 * result + (logType != null ? logType.hashCode() : 0);
    result = 31 * result + (level != null ? level.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Log{"
        + "timestamp="
        + timestamp
        + ", message='"
        + message
        + '\''
        + ", attributes="
        + attributes
        + ", serviceName='"
        + serviceName
        + '\''
        + ", logType='"
        + logType
        + '\''
        + ", level='"
        + level
        + '\''
        + '}';
  }

  /**
   * A class for holding the variables associated with a Log object and creating a new Log object
   * with those variables.
   */
  public static class LogBuilder {
    private long timestamp = System.currentTimeMillis();
    private String message;
    private Attributes attributes = new Attributes();
    private String serviceName; // service.name <- goes in attributes
    private String logType; // logtype <- goes in attributes
    private String level;

    /** The point in time (ms since UNIX epoch) that the log entry was created. */
    public LogBuilder timestamp(long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    /** The log line itself. */
    public LogBuilder message(String message) {
      this.message = message;
      return this;
    }

    /** Additional attributes associated with the log entry. */
    public LogBuilder attributes(Attributes attributes) {
      this.attributes = attributes;
      return this;
    }

    /** The name of the service which produced this log entry. */
    public LogBuilder serviceName(String serviceName) {
      this.serviceName = serviceName;
      return this;
    }

    /** The log level (eg. INFO, DEBUG, etc) for the log entry. */
    public LogBuilder level(String logLevel) {
      this.level = logLevel;
      return this;
    }

    /** The type of log entry. This can be helpful for querying your log entries in New Relic. */
    public LogBuilder logType(String type) {
      this.logType = type;
      return this;
    }

    /** Create the new {@link Log} entry. */
    public Log build() {
      Utils.verifyNonBlank(message, "A message is required for a log entry");
      return new Log(this);
    }

    /**
     * Will fill in the {@link #message(String)} with the stack trace from the Throwable. This will
     * also set the {@link #logType(String)} to be "stackTrace".
     */
    public LogBuilder stackTrace(Throwable e) {
      Utils.verifyNonNull(e);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      e.printStackTrace(new PrintStream(bytes));
      this.message = bytes.toString();
      this.logType = "stackTrace";
      return this;
    }

    @Override
    public String toString() {
      return "LogBuilder{"
          + "timestamp="
          + timestamp
          + ", message='"
          + message
          + '\''
          + ", attributes="
          + attributes
          + ", serviceName='"
          + serviceName
          + '\''
          + '}';
    }
  }
}