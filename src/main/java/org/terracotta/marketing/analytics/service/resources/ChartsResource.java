package org.terracotta.marketing.analytics.service.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.terracotta.marketing.analytics.chart.ChartPage;

@Path("/charts")
public class ChartsResource {
  
  @GET
  @Produces("text/html")
  public String showCharts() throws IOException, GeneralSecurityException, ParseException {
    final ChartPage page = new ChartPage();
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    page.render(out);
    return out.toString();
  }
}
