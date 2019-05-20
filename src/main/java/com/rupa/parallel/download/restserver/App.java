package com.rupa.parallel.download.restserver;

import com.rupa.parallel.download.rest.resource.ParallelDownloadResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class App {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(9090);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", ParallelDownloadResource.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            jettyServer.destroy();
        }
    }
}