package org.terracotta.marketing.analytics.chart;


import com.googlecode.charts4j.LegendPosition;

public class ChartConfig {
  private final LegendPosition legendPosition;
  private final String legendPrefix;
  private final double legendScale;
  private final String chartTitle;
  private final int height;
  private final int width;
  private final DateRange dateRange;

  public ChartConfig(final DateRange dateRange, final String chartTitle,
      final Dimension dimensions, final String legendPrefix,
      double legendScale, final LegendPosition legendPosition) {
    this.dateRange = dateRange;
    this.chartTitle = chartTitle;
    this.legendPrefix = legendPrefix;
    this.legendScale = legendScale;
    this.legendPosition = legendPosition;
    this.height = dimensions.getHeight();
    this.width = dimensions.getWidth();
  }

  public DateRange getDateRange() {
    return dateRange;
  }
  
  public String getChartTitle() {
    return chartTitle;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
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
