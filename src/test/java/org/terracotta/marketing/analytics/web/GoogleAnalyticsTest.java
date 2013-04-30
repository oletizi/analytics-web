package org.terracotta.marketing.analytics.web;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class GoogleAnalyticsTest {

  @Test
  public void test() throws IOException, GeneralSecurityException, ParseException {
    GoogleAnalytics ga = new GoogleAnalytics();
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = "2012-02-01";
    Date date = fmt.parse(dateString);
    System.out.println("Date: " + date);
    String dateToString = ga.dateToString(date);
    
    assertEquals(dateString, dateToString);

  }

}
