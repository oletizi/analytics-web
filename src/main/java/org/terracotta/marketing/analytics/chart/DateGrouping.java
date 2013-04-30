package org.terracotta.marketing.analytics.chart;

public class DateGrouping {

  public static DateGrouping Monthly = new DateGrouping("ga:year,ga:month");
  public static DateGrouping Daily = new DateGrouping(
      "ga:year,ga:month,ga:day");

  private String dimensions;

  private DateGrouping(final String dimensions) {
    this.dimensions = dimensions;
  }

  public String toString() {
    return dimensions;
  }
}