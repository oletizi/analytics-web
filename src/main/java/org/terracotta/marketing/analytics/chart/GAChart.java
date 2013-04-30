package org.terracotta.marketing.analytics.chart;

import static com.googlecode.charts4j.Color.DARKORANGE;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.terracotta.marketing.analytics.web.GoogleAnalytics;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Priority;
import com.googlecode.charts4j.Shape;

public class GAChart {

  private final LineChart chart;

  private GAChart(final ChartConfig chartConfig,
      final GAPlottable primaryPlottable, final GAPlottable secondaryPlottable) {
    
    final DateRange dateRange = chartConfig.getDateRange(); 
    
    final PlotConfig primaryPlotConfig = new PlotConfig(Priority.HIGH, LineStyle.MEDIUM_LINE,
        DARKORANGE, new TextConfig(12), Shape.CIRCLE,
        chartConfig.getLegendPrefix() + " " + dateRange,
        chartConfig.getLegendScale());
    
    final PlotConfig secondaryPlotConfig = new PlotConfig(Priority.LOW, LineStyle.THIN_LINE, Color.LIGHTBLUE,
        new TextConfig(10), Shape.DIAMOND, chartConfig
        .getLegendPrefix() + " " + dateRange.previousYear(),
        chartConfig.getLegendScale());
    
    final PlotFactory plotFactory = new PlotFactory(new GAPlottable[] { primaryPlottable,
        secondaryPlottable });

    final Line primaryPlot = plotFactory.newLineInstance(primaryPlottable, primaryPlotConfig);

    final Line secondaryPlot = plotFactory.newLineInstance(secondaryPlottable, secondaryPlotConfig);

    chart = GCharts.newLineChart(primaryPlot, secondaryPlot);
    chart.setSize(chartConfig.getWidth(), chartConfig.getHeight());

    chart.setTitle(chartConfig.getChartTitle());
    chart.setLegendPosition(chartConfig.getLegendPosition());

    chart.setGrid(100, 25, 5, 0);

    AxisLabels xLabels = AxisLabelsFactory.newAxisLabels(primaryPlottable.getDateStrings(new SimpleDateFormat("MMM yyyy")));
    chart.addXAxisLabels(xLabels);
  }

  public GAChart(final Metric metric, final DateRange dateRange,
      final DateGrouping dateGrouping, final ChartConfig chartConfig,
      final GoogleAnalytics ga) throws IOException {
    this(chartConfig, new GAPlottableFactory(ga).newInstance(metric,
        dateGrouping, dateRange), new GAPlottableFactory(ga).newInstance(
        metric, dateGrouping, dateRange.previousYear()));

    // GAPlottable plottable = pfactory.newInstance(metric, dateGrouping,
    // dateRange);
    // GAPlottable yoyPlottable = pfactory.newInstance(metric, dateGrouping,
    // dateRange.previousYear());

  }

  public String toURLString() {
    return chart.toURLString();
  }

  public static class TextConfig {
    private int size;

    public TextConfig(final int size) {
      this.size = size;
    }

    public int getSize() {
      return size;
    }
  }

  public static class PlotConfig {
    private Color color;
    private Shape markerShape;
    private String legend;
    private double legendScale;
    private LineStyle lineStyle;
    private Priority priority;
    private TextConfig markerTextConfig;

    public PlotConfig(final Priority priority, final LineStyle lineStyle,
        final Color color, final TextConfig markerTextConfig,
        final Shape markerShape, final String legend, double lagendScale) {
      this.priority = priority;
      this.lineStyle = lineStyle;
      this.color = color;
      this.markerTextConfig = markerTextConfig;
      this.markerShape = markerShape;
      this.legend = legend;
      this.legendScale = lagendScale;

    }

    public Priority getPriority() {
      return priority;
    }

    public LineStyle getLineStyle() {
      return lineStyle;
    }

    public TextConfig getMarkerTextConfig() {
      return markerTextConfig;
    }

    public Shape getMarkerShape() {
      return markerShape;
    }

    public Color getColor() {
      return color;
    }

    public String getLegend() {
      return legend;
    }

    public double getLegendScale() {
      return legendScale;
    }
  }

}
