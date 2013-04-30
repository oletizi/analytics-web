package org.terracotta.marketing.analytics.service;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.terracotta.marketing.analytics.service.resources.HelloWorldResource;
import org.terracotta.marketing.analytics.util.Logger;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class ServiceMain implements Runnable {
  private final URI BASE_URI;
  private final Logger logger = new Logger(getClass());
  private final HttpServer server;
  private boolean run = true;
  private boolean running = false;
  private Object startLock = new Object();
  private int port;

  public ServiceMain() throws IOException {
    this(9991, "localhost");
  }
  
  public ServiceMain(int port, String hostname) throws IOException {
    this.port = port;
    BASE_URI = UriBuilder.fromUri("http://" + hostname + "/")
        .port(port).build();
    logger.println("Starting web service on " + hostname + "...");
    String resourcesPackage = HelloWorldResource.class.getPackage().getName();
    logger.println("Configuring resources from package: " + resourcesPackage);
    final ResourceConfig rc = new PackagesResourceConfig(resourcesPackage);
    rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
    this.server = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);

  }

  public int getPort() {
    return port;
  }
  
  public void run() {
    try {
      synchronized (startLock) {
        server.start();
        running = true;
        startLock.notifyAll();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    synchronized (startLock) {
      while (run) {
        try {
          startLock.wait();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  public void waitUntilStarted() {
    synchronized (startLock) {
      while (! running) {
        try {
          startLock.wait();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  public void shutdown() {
    synchronized (startLock) {
      server.stop();
      running = false;
      run = false;
      startLock.notifyAll();
    }
  }

  public static void main(final String[] args) throws Exception {
    String hostname = "localhost";
    if (args.length > 0) {
      hostname = args[0];
    }
    ServiceMain main = new ServiceMain(9991, hostname);
    main.run();
  }
}