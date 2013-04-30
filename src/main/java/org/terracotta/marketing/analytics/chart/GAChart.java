package org.terracotta.marketing.analytics.chart;

import static com.googlecode.charts4j.Color.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.terracotta.marketing.analytics.web.GoogleAnalytics;

import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.Plot;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

public class GAChart {

  private GoogleAnalytics ga;
  private final DateRange dateRange;
  private NumberFormat nfmt = NumberFormat.getIntegerInstance();
  private int width = 750;
  private int height = 250;

  public GAChart(GoogleAnalytics ga, Date start, Date end) {
    this.ga = ga;
    dateRange = new DateRange(start, end);
  }

  public String renderPageviews(DateGrouping grouping) throws IOException {
    GAPlottable plottable = fetchPageviews(grouping, dateRange);
    GAPlottable yoyPlottable = fetchPageviews(grouping,
        dateRange.previousYear());

    Plot plot = preparePlot(plottable, new PlotConfig(LIGHTBLUE, Shape.CIRCLE,
        "Pageviews " + dateRange + " (thousands)"));

    Plot yoyPlot = preparePlot(yoyPlottable, new PlotConfig(DARKORANGE,
        Shape.DIAMOND, "Page views " + dateRange.previousYear()
            + " (thousands)"));

    LineChart chart = GCharts.newLineChart(plot, yoyPlot);
    chart.setSize(width, height);

    AxisLabels yLabels = AxisLabelsFactory.newNumericAxisLabels(0, plottable
        .getMax().doubleValue());
    chart.addYAxisLabels(yLabels);

    
    AxisLabels xLabels = AxisLabelsFactory.newAxisLabels(plottable
        .getDateStrings(new SimpleDateFormat("MMM yyyy")));
    chart.addXAxisLabels(xLabels);

    return chart.toURLString();
  }

  private GAPlottable fetchPageviews(DateGrouping grouping,
      DateRange theDateRange) throws IOException {
    Get get = ga.createGet(theDateRange.getStart(), theDateRange.getEnd(),
        Metric.Pageviews.toString());
    get.setDimensions(grouping.toString());
    GaData data = get.execute();
    GAPlottable plottable = new GAPlottable(data.getRows());
    return plottable;
  }

  private Plot preparePlot(GAPlottable plottable, PlotConfig plotConfig) {
    Plot plot = Plots.newPlot(plottable.getPlottableData());

    plot.setLegend(plotConfig.getLegend());

    plot.setColor(plotConfig.getColor());

    // add marker nodes
    plot.addShapeMarkers(plotConfig.getMarkerShape(), plotConfig.getColor(), 10);

    // add text markers
    List<? extends Number> values = plottable.getData();

    for (int i = 0; i < values.size(); i++) {
      String text = nfmt.format(values.get(i).doubleValue() / 1000d);
      plot.addTextMarker(text, plotConfig.getColor(), 10, i);
    }
    return plot;
  }

  public static class PlotConfig {
    private Color color;
    private Shape markerShape;
    private String legend;

    public PlotConfig(final Color color, Shape markerShape, String legend) {
      this.color = color;
      this.markerShape = markerShape;
      this.legend = legend;

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
  }

  public static class DateGrouping {

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

  public static class Metric {
    public static Metric Pageviews = new Metric("ga:pageviews");
    private String metric;

    private Metric(final String metric) {
      this.metric = metric;
    }

    public String toString() {
      return metric;
    }
  }

  public static void main(String[] args) throws IOException,
      GeneralSecurityException, ParseException {
    Plot plot = Plots.newPlot(Data.newData(0, 66.6, 33.3, 100));
    LineChart chart = GCharts.newLineChart(plot);
    chart.addHorizontalRangeMarker(33.3, 66.6, LIGHTBLUE);
    chart.setGrid(33.3, 33.3, 3, 3);
    chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(
        Arrays.asList("Peak", "Valley"), Arrays.asList(33.3, 66.6)));
    chart.addYAxisLabels(AxisLabelsFactory.newNumericAxisLabels(0, 33.3, 66.6,
        100));
    String url = chart.toURLString();
    System.err.println(url);

    PrintWriter out = new PrintWriter(new FileWriter("/tmp/charts.html"));
    out.println("<html><body>");

    out.println("<h1>Web State</h1>");
    out.println("<h2>Monthly Page Views</h2>");

    DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
    GoogleAnalytics ga = new GoogleAnalytics();
    GAChart gaChart = new GAChart(ga, dfmt.parse("2013-01-01"),
        dfmt.parse("2013-04-30"));

    String monthlyPageviews = gaChart.renderPageviews(DateGrouping.Monthly);

    out.println("<img src=\"" + monthlyPageviews + "\"/>");
    out.println("</body></html>");
    out.flush();
    out.close();
  }
}
