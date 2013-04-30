package org.terracotta.marketing.analytics.chart;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
  public Date previousYear(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.YEAR, -1);
    return cal.getTime();
  }
}