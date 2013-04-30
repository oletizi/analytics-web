package org.terracotta.marketing.analytics.chart;

import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;

public class CommonScale {

  private Double max;
  
  public CommonScale(final GAPlottable[] dataSets) {
    for (GAPlottable series : dataSets) {
      if (max == null) {
        max = series.getMax().doubleValue();
      } else {
        max = max > series.getMax().doubleValue() ? max : series.getMax().doubleValue();
      }
    }
  }

  public Data scale(GAPlottable plottable) {
    return DataUtil.scaleWithinRange(0, max, plottable.getData());
  }
}
