package org.terracotta.marketing.analytics.chart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRange {

  private final Date start;
  private final Date end;
  private final DateUtil dateUtil = new DateUtil();
  private final DateFormat hfmt = new SimpleDateFormat("MMM dd yyyy");
  
  public DateRange(final Date start, final Date end) {
    this.start = start;
    this.end = end;
  }

  public DateRange previousYear() {
    return new DateRange(dateUtil.previousYear(start), dateUtil.previousYear(end));
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  public String toString() {
    return hfmt.format(start) + " - " + hfmt.format(end);
  }
  
}
