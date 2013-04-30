package org.terracotta.marketing.analytics.chart;

import com.googlecode.charts4j.LegendPosition;

public class ChartConfig {
  private final LegendPosition legendPosition;
  private final String legendPrefix;
  private final double legendScale;
  private String chartTitle;

  public ChartConfig(final String chartTitle, final String legendPrefix, double legendScale, final LegendPosition legendPosition) {
    this.chartTitle = chartTitle;
    this.legendPrefix = legendPrefix;
    this.legendScale = legendScale;
    this.legendPosition = legendPosition;
  }

  public String getChartTitle() {
    return chartTitle;
  }
  
  public String getLegendPrefix() {
    return legendPrefix;
  }
  
  public LegendPosition getLegendPosition() {
    return legendPosition;
  }

  public double getLegendScale() {
    return legendScale;
  }
}
