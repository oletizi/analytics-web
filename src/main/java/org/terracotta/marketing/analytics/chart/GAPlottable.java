package org.terracotta.marketing.analytics.chart;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;

public class GAPlottable {

  private final NumberFormat fmt = NumberFormat.getInstance();
  private final List<Number> data = new ArrayList<Number>();
  private final List<Date> dates = new ArrayList<Date>();
  private DateFormat dfmt;
  private Number max;
  private Number min;
  
  public GAPlottable(final List<List<String>> series) {
    for (List<String> row : series) {
      StringBuffer dateString = new StringBuffer();
      StringBuffer dateFormat = new StringBuffer("yyyy-MM");
      String year = row.get(0);
      dateString.append(year);
      String month = row.get(1);
      dateString.append("-" + month);
      
      if (row.size() > 3) {
        String day = row.get(2);
        dateString.append("-" + day);
        dateFormat.append("-dd");
      }
      
      if (dfmt == null) {
        dfmt = new SimpleDateFormat(dateFormat.toString());
      }
      try {
        dates.add(dfmt.parse(dateString.toString()));
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
      
      String datum = row.get(row.size() - 1);
      try {
        Number parsed = fmt.parse(datum);
        if (max == null) {
          max = parsed;
        } else if (max.doubleValue() < parsed.doubleValue()) {
          max = parsed;
        }
        
        if (min == null) {
          min = parsed;
        } else if (min.doubleValue() > parsed.doubleValue()) {
          min = parsed;
        }
        data.add(parsed);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Number getMax() {
    return max;
  }
  
  public Number getMin() {
    return min;
  }
  
  public List<? extends Number> getData() {
    return data;
  }

  public Data getPlottableData() {
    return DataUtil.scaleWithinRange(0, max.doubleValue(), data);
  }

  public List<String> getDateStrings(final SimpleDateFormat myDateFormat) {
    List<String> rv = new ArrayList<String>(dates.size());
    for (Date date : dates) {
      rv.add(myDateFormat.format(date));
    }
    return rv;
  }
  
}
