package org.terracotta.marketing.analytics.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.terracotta.marketing.analytics.web.auth.Auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;

/**
 * For help on Google Analytics API, see:
 * https://code.google.com/p/google-api-java
 * -client/source/browse/analytics-cmdline
 * -sample/src/main/java/com/google/api/services
 * /samples/analytics/cmdline/HelloAnalyticsApiSample.java?repo=samples
 * 
 * @author orion
 * 
 */

public class GoogleAnalytics {
  private static final String PROFILE_ID = "46443769";
  private static final String TABLE_ID = "ga:" + PROFILE_ID;
  private static final String APPLICATION_NAME = "Orion Web Stats";
  private final Analytics analytics;
  private final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
  
  public GoogleAnalytics() throws IOException, GeneralSecurityException {
    Credential credential = Auth.authorize();

    JsonFactory jsonFactory = new JacksonFactory();
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    Analytics.Builder builder = new Analytics.Builder(httpTransport,
        jsonFactory, credential);
    builder.setApplicationName(APPLICATION_NAME);
    analytics = builder.build();
  }

  public Get createGet(final Date startDate, final Date endDate, final String metrics) throws IOException {
    return analytics.data().ga().get(TABLE_ID, dateToString(startDate), dateToString(endDate), metrics);
  }
  
  private void example() throws GeneralSecurityException, IOException {

    GaData results = analytics.data().ga().get(TABLE_ID,
    // Table Id. ga: + profile id.
        "2013-01-01", // Start date.
        "2013-01-14", // End date.
        "ga:visits")
        // Metrics.
        .setDimensions("ga:source,ga:keyword").setSort("-ga:visits,ga:source")
        .setFilters("ga:medium==organic").setMaxResults(25).execute();
    // Print column headers.
    for (ColumnHeaders header : results.getColumnHeaders()) {
      System.out.printf("%30s", header.getName());
    }
    System.out.println();

    // Print actual data.
    for (List<String> row : results.getRows()) {
      for (String column : row) {
        System.out.printf("%30s", column);
      }
      System.out.println();
    }
  }

  private void example2() throws IOException {
    Get get = analytics.data().ga().get(TABLE_ID, "2012-02-01", "2013-04-30", "ga:pageviews");
    get.setDimensions("ga:year,ga:month");
    GaData results = get.execute();
    for (ColumnHeaders header: results.getColumnHeaders()) {
      System.out.printf("%30s", header.getName());
    }
    System.out.println();
    for (List<String> row : results.getRows()) {
      for (String column : row) {
        System.out.printf("%30s", column);
      }
      System.out.println();
    }
  }

  public static void main(String[] args) throws GeneralSecurityException,
      IOException {
    new GoogleAnalytics().example2();
  }

  public String dateToString(Date date) {
    return fmt.format(date);
  }
}
