package org.terracotta.marketing.analytics.chart;

import java.text.NumberFormat;
import java.util.List;

import org.terracotta.marketing.analytics.chart.GAChart.PlotConfig;

import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.Marker;
import com.googlecode.charts4j.Markers;
import com.googlecode.charts4j.Plots;

public class PlotFactory {
  private final CommonScale commonScale;
  private final NumberFormat nfmt = NumberFormat.getIntegerInstance();
  
  public PlotFactory(final GAPlottable[] dataSets) {
    this.commonScale = new CommonScale(dataSets);
  }
  
  public Line newLineInstance(final GAPlottable plottable, final PlotConfig plotConfig) {
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
      String text = nfmt.format(values.get(i).doubleValue()
          * plotConfig.getLegendScale());
      Marker marker = Markers.newTextMarker(text, plotConfig.getColor(),
          plotConfig.getMarkerTextConfig().getSize(), plotConfig.getPriority());
      plot.addMarker(marker, i);
    }
    return plot;
  }
  
  private static class CommonScale {

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
}
