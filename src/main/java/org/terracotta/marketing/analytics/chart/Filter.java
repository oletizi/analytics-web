package org.terracotta.marketing.analytics.chart;

public class Filter {
  private String filterOn;
  private String filterExpression;
  private boolean isNull;

  private Filter(final String filterOn, final String filterExpression, boolean isNull) {
    this.filterOn = filterOn;
    this.filterExpression = filterExpression;
    this.isNull = isNull;
  }

  public boolean isNull() {
    return isNull;
  }
  
  public String toString() {
    return isNull() ? "" : filterOn + "=~" + filterExpression;
  }
  
  public static class FilterFactory {
    
    public Filter newNullFilter() {
      return new Filter("","", true);
    }
    
    public Filter newPagePathInstance(final String expression) {
      return new Filter("ga:pagePath", expression, false);
    }
  }
}
