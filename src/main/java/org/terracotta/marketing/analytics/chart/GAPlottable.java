package org.terracotta.marketing.analytics.chart;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;

public class GAPlottable {

  private final NumberFormat fmt = NumberFormat.getInstance();
  private final List<Number> data;// = new ArrayList<Number>();
  private final List<Date> dates; // = new ArrayList<Date>();
  private final List<String> dateStrings;
  private DateFormat dfmt;
  private Number max;
  private Number min;

  /**
   * Expected data and dates to be chunked up into quarters.
   * 
   * @param data
   * @param dates
   */
  private GAPlottable(List<Number> data, List<String> dateStrings) {
    this.data = data;
    this.dateStrings = dateStrings;
    this.dates = Collections.emptyList();
    for (Number datum : data) {
      if (max == null) {
        max = datum;
      } else {
        max = max.doubleValue() > datum.doubleValue() ? max : datum;
      }

      if (min == null) {
        min = datum;
      } else {
        min = min.doubleValue() < datum.doubleValue() ? min : datum;
      }
    }
  }

  public GAPlottable(final List<List<String>> series) {
    data = new ArrayList<Number>();
    dates = new ArrayList<Date>();
    dateStrings = Collections.emptyList();
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

  // public Data getPlottableData() {
  // return DataUtil.scaleWithinRange(0, max.doubleValue(), data);
  // }

  public List<String> getDateStrings(final DateFormat myDateFormat) {
    List<String> rv = new ArrayList<String>(data.size());
    if (dateStrings.size() > 0) {
      rv.addAll(dateStrings);
    } else {
      for (Date date : dates) {
        rv.add(myDateFormat.format(date));
      }
    }
    return rv;
  }

  public GAPlottable groupByCalendarQuarter() {
    List<Number> qtrData = new ArrayList<Number>();
    List<String> qtrDateStrings = new ArrayList<String>();
    Calendar cal = Calendar.getInstance();
    double sum = 0;
    for (int i = 0; i < data.size(); i++) {
      double value = data.get(i).doubleValue();
      cal.setTime(dates.get(i));
      int month = cal.get(Calendar.MONTH);



      sum += value;
      
      if ((month + 1) % 3 == 0 || i + 1 == data.size()) {
        // this is the last month of the quarter or the last month in the series
        // so we have all of the data for the current quarter.
        qtrData.add(sum);
        
        // Figure out which quarter it is and add it to the datestrings
        String quarterName = "";
        if (month >= 0 && month < 3) {
          quarterName = "Q1";
        } else if ( month >= 3 && month < 6 ) {
          quarterName = "Q2";
        } else if (month >= 6 && month < 9) {
          quarterName = "Q3";
        } else {
          quarterName = "Q4";
        }
        qtrDateStrings.add(quarterName + " " + cal.get(Calendar.YEAR));

        // then reset the sum
        sum = 0;
      }

    }
    return new GAPlottable(qtrData, qtrDateStrings);
  }
}
