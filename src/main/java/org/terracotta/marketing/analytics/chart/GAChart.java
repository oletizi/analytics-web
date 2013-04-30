package org.terracotta.marketing.analytics.chart;

import static com.googlecode.charts4j.Color.DARKORANGE;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.terracotta.marketing.analytics.web.GoogleAnalytics;

import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Marker;
import com.googlecode.charts4j.Markers;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Priority;
import com.googlecode.charts4j.Shape;

public class GAChart {

  private final GoogleAnalytics ga;
  private final int width = 750;
  private final int height = 250;
  private final LineChart chart;
  private NumberFormat nfmt = NumberFormat.getIntegerInstance();
  private CommonScale commonScale;

  public GAChart(final Metric metric, final DateRange dateRange,
      final DateGrouping dateGrouping, final ChartConfig chartConfig,
      final GoogleAnalytics ga) throws IOException {
    this.ga = ga;
    GAPlottable plottable = fetchMetricSeries(metric, dateGrouping, dateRange);
    GAPlottable yoyPlottable = fetchMetricSeries(metric, dateGrouping,
        dateRange.previousYear());
    
    commonScale = new CommonScale(new GAPlottable[] { plottable, yoyPlottable} );

    Line plot = preparePlot(plottable, new PlotConfig(Priority.HIGH, LineStyle.MEDIUM_LINE, DARKORANGE, new TextConfig(12), Shape.CIRCLE,
        chartConfig.getLegendPrefix() + " " + dateRange, chartConfig.getLegendScale() ));
    
    Line yoyPlot = preparePlot(yoyPlottable, new PlotConfig(Priority.LOW, LineStyle.THIN_LINE, Color.LIGHTBLUE, new TextConfig(10),
        Shape.DIAMOND, chartConfig.getLegendPrefix() + " " + dateRange.previousYear(), chartConfig.getLegendScale()));

    chart = GCharts.newLineChart(plot, yoyPlot);
    chart.setSize(width, height);
    
    chart.setTitle(chartConfig.getChartTitle());
    chart.setLegendPosition(chartConfig.getLegendPosition());

    chart.setGrid(100, 25, 5, 0);
    
    //AxisLabels yLabels = AxisLabelsFactory.newNumericRangeAxisLabels(0, plottable.getMax().doubleValue());
    //chart.addYAxisLabels(yLabels);

    AxisLabels xLabels = AxisLabelsFactory.newAxisLabels(plottable
        .getDateStrings(new SimpleDateFormat("MMM yyyy")));
    chart.addXAxisLabels(xLabels);
  }

  public String toURLString() {
    return chart.toURLString();
  }

  private GAPlottable fetchMetricSeries(final Metric metric, final DateGrouping grouping, final DateRange dateRange)
      throws IOException {
    Get get = ga.createGet(dateRange.getStart(), dateRange.getEnd(), metric.toString());
    get.setDimensions(grouping.toString());
    GaData data = get.execute();
    GAPlottable plottable = new GAPlottable(data.getRows());
    return plottable;
  }

  private Line preparePlot(GAPlottable plottable, PlotConfig plotConfig) {
    Line plot = Plots.newLine(commonScale.scale(plottable));

    plot.setLineStyle(plotConfig.getLineStyle());
    plot.setPriority(plotConfig.getPriority());
    plot.setLegend(plotConfig.getLegend());

    plot.setColor(plotConfig.getColor());

    // add marker nodes
    plot.addShapeMarkers(plotConfig.getMarkerShape(), plotConfig.getColor(), 10);
    
    // add text markers
    List<? extends Number> values = plottable.getData();

    for (int i = 0; i < values.size(); i++) {
      String text = nfmt.format(values.get(i).doubleValue() * plotConfig.getLegendScale());
      Marker marker = Markers.newTextMarker(text, plotConfig.getColor(), plotConfig.getMarkerTextConfig().getSize(), plotConfig.getPriority());
      plot.addMarker(marker, i);
      //plot.addTextMarker(text, plotConfig.getColor(), plotConfig.getMarkerTextConfig().getSize(), i);
    }
    return plot;
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
        final Color color, final TextConfig markerTextConfig, final Shape markerShape, final String legend,
        double lagendScale) {
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
