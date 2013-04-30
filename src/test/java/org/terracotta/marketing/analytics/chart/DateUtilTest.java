package org.terracotta.marketing.analytics.chart;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DateUtilTest {

  @Test
  public void test() throws ParseException {
    DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
    
    Date original = dfmt.parse("2013-02-01");
    Date yearAgoExpected = dfmt.parse("2012-02-01");
    DateUtil util = new DateUtil();
  
    Date yearAgo = util.previousYear(original);
    assertEquals(yearAgoExpected, yearAgo);
  }

}
