package org.terracotta.marketing.analytics.chart;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

  public ChartPage() throws IOException, GeneralSecurityException, ParseException {
    buf = new ByteArrayOutputStream();
    out = new PrintWriter(buf);
    
    DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
    GoogleAnalytics ga = new GoogleAnalytics();
    Metric metric = Metric.Pageviews;
    DateRange dateRange = new DateRange(dfmt.parse("2012-05-01"), dfmt.parse("2013-04-30"));
    DateGrouping dateGrouping = DateGrouping.Monthly;
    
    final Dimension dim = new Dimension(750, 300);
    
    ChartConfig chartConfig = new ChartConfig(dateRange, "Monthly Pageviews", dim, "Pageviews (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart pageViewChart = new GAChart(metric, dateRange, dateGrouping, chartConfig, ga);
    
    chartConfig = new ChartConfig(dateRange, "Monthly Unique Visitors", dim, "Unique Visitors (thousands)", 0.001, LegendPosition.BOTTOM);
    GAChart visitors = new GAChart(Metric.Visitors, dateRange, dateGrouping, chartConfig, ga);
    
    out.println("<html><body>");
    out.println("<h1>Web Stats</h1>");
    
    out.println("<h2>Monthly Page Views</h2>");
    out.println(getImg(pageViewChart));
    
    out.println("<h2>Monthly Visitors</h2>");
    out.println(getImg(visitors));
    
    out.println("</body></html>");
  }

  private String getImg(GAChart chart){
    return "<img src=\"" + chart.toURLString() + "\"/>";
  }
  
  private void render(final String path) throws IOException {
    FileOutputStream fout = new FileOutputStream(path);
    out.flush();
    buf.flush();
    buf.writeTo(fout);
  }

  public static void main(String[] args) throws IOException, GeneralSecurityException, ParseException {
    ChartPage page = new ChartPage();
    String outfile = "/tmp/charts.html";
    page.render(outfile);
    System.err.println("Wrote page to " + outfile); 
  }

}
