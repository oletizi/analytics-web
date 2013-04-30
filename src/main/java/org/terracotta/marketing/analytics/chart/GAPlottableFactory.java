package org.terracotta.marketing.analytics.chart;

import java.io.IOException;

import org.terracotta.marketing.analytics.web.GoogleAnalytics;

import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;

public class GAPlottableFactory {

  private GoogleAnalytics ga;

  public GAPlottableFactory(final GoogleAnalytics ga) {
    this.ga = ga;
  }

  public GAPlottable newInstance(final Metric metric,
      final DateGrouping grouping, final DateRange dateRange)
      throws IOException {
    Get get = ga.createGet(dateRange.getStart(), dateRange.getEnd(),
        metric.toString());
    get.setDimensions(grouping.toString());
    GaData data = get.execute();
    GAPlottable plottable = new GAPlottable(data.getRows());
    return plottable;
  }

}