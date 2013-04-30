package org.terracotta.marketing.analytics.web.auth;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.common.base.Joiner;

public class Auth {
  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
  private static final HttpTransport HTTP_TRANSPORT;
  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  private static final String OAUTH2_URL = "https://accounts.google.com/o/oauth2/auth";
  private static final String RESPONSE_TYPE = "code";
  private static final String CLIENT_ID = "1050363002592.apps.googleusercontent.com";
  private static final String REDIRECT_URI = "http://localhost";
  private static final String SCOPE = "https://www.googleapis.com/auth/analytics.readonly";
  private static final String STATE = "foo";

  private static final String CODE = "4/1aufHM_kSdqEE2LWX-GTgUzd_jJo.QrgrwLO32sYSEnp6UAPFm0G83IGvfAI";

  public static final String generateAuthURL() {
    URLHelper url = new URLHelper(OAUTH2_URL);
    url.addParam("response_type", RESPONSE_TYPE);
    url.addParam("client_id", CLIENT_ID);
    url.addParam("redirect_uri", REDIRECT_URI);
    url.addParam("scope", SCOPE);
    url.addParam("state", STATE);

    return url.toString();
  }

  public static Credential authorize() throws IOException {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        Auth.class.getResourceAsStream("/client_secrets.json"));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out
          .println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
              + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(new File(
        System.getProperty("user.home"), ".credentials/analytics.json"),
        JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY))
        .setCredentialStore(credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
        .authorize("user");
  }

  public static void main(final String[] args) {
    System.out.println(generateAuthURL());
  }

  static class URLHelper {
    private final Map<String, String> params = new HashMap<String, String>();
    private String baseURL;

    public URLHelper(final String baseURL) {
      this.baseURL = baseURL;
    }

    public void addParam(final String param, final Object value) {
      try {
        params.put(param, URLEncoder.encode("" + value, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    public String toString() {
      final StringBuffer buf = new StringBuffer();
      buf.append(baseURL);

      if (params.size() > 0) {
        buf.append("?");
        Joiner.MapJoiner joiner = Joiner.on("&").withKeyValueSeparator("=");
        buf.append(joiner.join(params));
      }
      return buf.toString();
    }
  }
}
