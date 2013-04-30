package org.terracotta.marketing.analytics.chart;

public class Metric {
  public static final Metric Visitors = new Metric("ga:visitors");
  public static Metric Pageviews = new Metric("ga:pageviews");
  private String metric;

  private Metric(final String metric) {
    this.metric = metric;
  }

  public String toString() {
    return metric;
  }
}