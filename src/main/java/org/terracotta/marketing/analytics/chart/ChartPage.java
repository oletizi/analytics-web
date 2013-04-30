package org.terracotta.marketing.analytics.chart;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    Metric metric = Metric.Pageviews;
    DateRange dateRange = new DateRange(dfmt.parse("2012-05-01"),
        dfmt.parse("2013-04-30"));
    DateRange quarterlyDateRange = new DateRange(dfmt.parse("2012-04-01"), dfmt.parse("2013-03-31"));
    DateGrouping dateGrouping = DateGrouping.Monthly;

    final Dimension dim = new Dimension(750, 300);
    
    // MONTHLY PAGEVIEWS
    ChartConfig chartConfig = new ChartConfig(dateRange, "Monthly Pageviews",
        dim, "Pageviews (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart monthlyPageViews = new GAChart(metric, dateRange, dateGrouping,
        chartConfig, ga);

    // QUARTERLY PAGEVIEWS
    chartConfig = new ChartConfig(quarterlyDateRange, "Quarterly Pageviews", dim,
        "Pageviews (thousands)", 0.001, LegendPosition.BOTTOM);
    
    GAChart quarterlyPageViews = new GAChart(metric, quarterlyDateRange, dateGrouping, chartConfig, ga).groupByCalendarQuarter(chartConfig);

    // MONTHLY VISITORS
    chartConfig = new ChartConfig(dateRange, "Monthly Visitors", dim,
        "Visitors (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart monthlyVisitors = new GAChart(Metric.Visitors, dateRange,
        dateGrouping, chartConfig, ga);
    
    // QUARTERLY VISITORS
    chartConfig = new ChartConfig(quarterlyDateRange, "Quarterly Visitors", dim, "Visitors (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart quarterlyVisitors = new GAChart(metric, quarterlyDateRange, dateGrouping, chartConfig, ga).groupByCalendarQuarter(chartConfig);

    out.println("<html><body>");
    out.println("<h1>Web Stats</h1>");

    out.println("<h2>Page Views</h2>");
    out.println(getImg(monthlyPageViews));
    out.println(getImg(quarterlyPageViews));

    out.println("<h2>Visitors</h2>");
    out.println(getImg(monthlyVisitors));
    out.println(getImg(quarterlyVisitors));

    out.println("</body></html>");
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
