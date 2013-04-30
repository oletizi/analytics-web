package org.terracotta.marketing.analytics.util;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Logger {
  
  private final PrintWriter out;
  private final DateFormat dfmt;
  
  public Logger(final Class<?> clazz) {
    out = new PrintWriter(System.out);
    dfmt = new SimpleDateFormat("HH:mm:ss");
  }

  public void println(final Object o) {
    out.println(preamble() + o + "");
    out.flush();
  }
  
  public void format(final String format, final Object... o) {
    out.format(preamble() + format, o);
    out.flush();
  }
  
  private String preamble() {
    return dfmt.format(System.currentTimeMillis()) + " -- ";
  }
  
  public static final Logger getInstance(final Class<?> clazz) {
    return new Logger(clazz);
  }
  
}
