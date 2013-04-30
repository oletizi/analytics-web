package org.terracotta.marketing.analytics.chart;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DateRangeTest {

  @Test
  public void test() throws ParseException {
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    Date start = fmt.parse("2013-04-01");
    Date end = fmt.parse("2013-04-30");
    
    DateRange range = new DateRange(start, end);
    DateRange previousYear = range.previousYear();
    
    Date previousStart = previousYear.getStart();
    Date previousEnd = previousYear.getEnd();
    
    Date expectedPreviousStart = fmt.parse("2012-04-01");
    Date expectedPreviousEnd = fmt.parse("2012-04-30");
    
    assertEquals(expectedPreviousStart, previousStart);
    assertEquals(expectedPreviousEnd, previousEnd);
  }

}
