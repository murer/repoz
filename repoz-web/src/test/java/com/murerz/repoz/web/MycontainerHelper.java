package com.murerz.repoz.web;

import javax.naming.NamingException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class MycontainerHelper {

	private static final Object MUTEX = new Object();

	private static MycontainerHelper me;

	private ContainerBuilder builder;

	private JettyServerDeployer webServer;

	public static MycontainerHelper me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					MycontainerHelper ret = new MycontainerHelper();
					ret.boot();
					me = ret;
				}
			}
		}
		return me;
	}

	private void boot() {
		try {
			System.setProperty("java.naming.factory.initial", "com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");
			builder = new ContainerBuilder();
			builder.deployVMShutdownHook();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUp() {
		bootWeb();
	}

	private void bootWeb() {
		webServer = builder.createDeployer(JettyServerDeployer.class);
		webServer.setName("WebServer");
		ContextWebServer webContext = webServer.createContextWebServer();
		webContext.setContext("/");
		webContext.setResources("src/main/webapp");
		webContext.getFilters().add(new FilterDesc(LogFilter.class, "/*"));
		webServer.deploy();
	}

	public int bind(int port) {
		try {
			SelectChannelConnector connector = new SelectChannelConnector();
			connector.setPort(port);
			connector.setMaxIdleTime(30000);
			Server jetty = webServer.getServer();
			jetty.addConnector(connector);
			connector.start();
			return connector.getLocalPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void unbindPort(int port) {
		try {
			Server jetty = webServer.getServer();
			Connector[] connectors = jetty.getConnectors();
			for (Connector connector : connectors) {
				if (connector.getLocalPort() == port) {
					connector.stop();
					jetty.removeConnector(connector);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void tearDown() {

	}

}
