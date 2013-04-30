package org.terracotta.marketing.analytics.chart;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.terracotta.marketing.analytics.chart.Filter.FilterFactory;
import org.terracotta.marketing.analytics.web.GoogleAnalytics;

import com.googlecode.charts4j.LegendPosition;

public class ChartPage {
  private final ByteArrayOutputStream buf;
  private final PrintWriter out;

  public ChartPage() throws IOException, GeneralSecurityException,
      ParseException {
    buf = new ByteArrayOutputStream();
    out = new PrintWriter(buf);

    DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
    GoogleAnalytics ga = new GoogleAnalytics();
    DateRange dateRange = new DateRange(dfmt.parse("2012-05-01"),
        dfmt.parse("2013-04-30"));
    DateRange quarterlyDateRange = new DateRange(dfmt.parse("2012-04-01"),
        dfmt.parse("2013-03-31"));
    DateGrouping dateGrouping = DateGrouping.Monthly;

    final Dimension dim = new Dimension(750, 300);

    final FilterFactory ffactory = new FilterFactory();
    final Filter nullFilter = ffactory.newNullFilter();

    // MONTHLY PAGEVIEWS
    ChartConfig chartConfig = new ChartConfig(dateRange, "Monthly Pageviews",
        dim, "Pageviews (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart monthlyPageViews = new GAChart(Metric.Pageviews, nullFilter,
        dateRange, dateGrouping, chartConfig, ga);

    // QUARTERLY PAGEVIEWS
    chartConfig = new ChartConfig(quarterlyDateRange, "Quarterly Pageviews",
        dim, "Pageviews (thousands)", 0.001, LegendPosition.BOTTOM);

    GAChart quarterlyPageViews = new GAChart(Metric.Pageviews, nullFilter,
        quarterlyDateRange, dateGrouping, chartConfig, ga)
        .groupByCalendarQuarter(chartConfig);

    // MONTHLY VISITORS
    chartConfig = new ChartConfig(dateRange, "Monthly Visitors", dim,
        "Visitors (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart monthlyVisitors = new GAChart(Metric.Visitors, nullFilter,
        dateRange, dateGrouping, chartConfig, ga);

    // QUARTERLY VISITORS
    chartConfig = new ChartConfig(quarterlyDateRange, "Quarterly Visitors",
        dim, "Visitors (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart quarterlyVisitors = new GAChart(Metric.Visitors, nullFilter,
        quarterlyDateRange, dateGrouping, chartConfig, ga)
        .groupByCalendarQuarter(chartConfig);

    // MONTHLY DOWNLOADS
    // BigMemory Max
    // TODO: use helper method
    chartConfig = new ChartConfig(dateRange, "Monthly BigMemory Max Downloads",
        dim, "BigMemory Max Downloads", 1, LegendPosition.BOTTOM);
    Filter bmmFilter = ffactory.newPagePathInstance("/licensing/bigmemorymax");
    GAChart bmmDownloads = new GAChart(Metric.Pageviews, bmmFilter, dateRange,
        dateGrouping, chartConfig, ga);

    chartConfig = new ChartConfig(dateRange,
        "Quarterly BigMemory Max Downloads", dim, "BigMemory Max Downloads", 1,
        LegendPosition.BOTTOM);
    GAChart quarterlyBmmDownloads = new GAChart(Metric.Pageviews, bmmFilter,
        quarterlyDateRange, dateGrouping, chartConfig, ga)
        .groupByCalendarQuarter(chartConfig);

    // BigMemory Go
    // TODO: use helper method
    chartConfig = new ChartConfig(dateRange, "Monthly BigMemory Go Downloads",
        dim, "BigMemory Go Downloads", 1, LegendPosition.BOTTOM);
    Filter bmgFilter = ffactory.newPagePathInstance("/licensing/bigmemorygo");
    GAChart bmgDownloads = new GAChart(Metric.Pageviews, bmgFilter, dateRange,
        dateGrouping, chartConfig, ga);

    chartConfig = new ChartConfig(dateRange,
        "Quarterly BigMemory Go Downloads", dim, "BigMemory Go Downloads", 1,
        LegendPosition.BOTTOM);
    GAChart quarterlyBmgDownloads = new GAChart(Metric.Pageviews, bmgFilter,
        quarterlyDateRange, dateGrouping, chartConfig, ga)
        .groupByCalendarQuarter(chartConfig);

    // Enterprise Ehcache
    
    String filterExpression = "/downloads/enterprise-ehcache";
    GAChart eecDownloads = newMonthlyDownloadChart(dateRange, dim,
        "Monthly Enterprise Ehcache Downloads", "Enterprise Ehcache Downloads",
        1, filterExpression, ga);
    GAChart quarterlyEecDownloads = newQuarterlyDownloadChart(quarterlyDateRange, dim,
        "Quarterly Enterprise Ehcache Downloads",
        "Enterprise Ehcache Downloads", 1, filterExpression, ga);

    out.println("<html><body>");
    out.println("<h1>Web Stats</h1>");

    out.println("<h2>Page Views</h2>");
    out.println(getImg(monthlyPageViews));
    out.println(getImg(quarterlyPageViews));

    out.println("<h2>Visitors</h2>");
    out.println(getImg(monthlyVisitors));
    out.println(getImg(quarterlyVisitors));

    out.println("<h2>Downloads</h2>");
    out.println("<h3>BigMemory Max</h3>");
    out.println(getImg(bmmDownloads));
    out.println(getImg(quarterlyBmmDownloads));

    out.println("<h3>BigMemory Go</h3>");
    out.println(getImg(bmgDownloads));
    out.println(getImg(quarterlyBmgDownloads));

    out.println("<h3>Enterprise Ehcache</h3>");
    out.println(getImg(eecDownloads));
    out.println(getImg(quarterlyEecDownloads));

    out.println("</body></html>");
  }

  private GAChart newMonthlyDownloadChart(final DateRange dateRange, Dimension dim,
      String title, String legend, double markerScalingFactor,
      String filterExpression, GoogleAnalytics ga) throws IOException {
    return newDownloadChart(dateRange, dim, title, legend, markerScalingFactor, filterExpression, ga, false);
  }

  private GAChart newQuarterlyDownloadChart(final DateRange dateRange, Dimension dim,
      String title, String legend, double markerScalingFactor,
      String filterExpression, GoogleAnalytics ga) throws IOException {
    return newDownloadChart(dateRange, dim, title, legend, markerScalingFactor, filterExpression, ga, true);
  }

  private GAChart newDownloadChart(final DateRange dateRange, Dimension dim, String title, String legend,
      double markerScalingFactor, String filterExpression, GoogleAnalytics ga, boolean quarterly) throws IOException {
    ChartConfig chartConfig = new ChartConfig(dateRange, title, dim, legend,
        markerScalingFactor, LegendPosition.BOTTOM);
    GAChart chart = new GAChart(Metric.Pageviews,
        new FilterFactory().newPagePathInstance(filterExpression), dateRange,
        DateGrouping.Monthly, chartConfig, ga);
    return quarterly ? chart.groupByCalendarQuarter(chartConfig) : chart;
    
  }
  
  private String getImg(GAChart chart) {
    return "<p><img src=\"" + chart.toURLString() + "\"/></p>";
  }

  public void render(final String path) throws IOException {
    render(new FileOutputStream(path));
  }

  public void render(final OutputStream output) throws IOException {
    out.flush();
    buf.flush();
    buf.writeTo(output);
  }

  public static void main(String[] args) throws IOException,
      GeneralSecurityException, ParseException {
    ChartPage page = new ChartPage();
    String outfile = "/tmp/charts.html";
    page.render(outfile);
    System.err.println("Wrote page to " + outfile);
  }

}
