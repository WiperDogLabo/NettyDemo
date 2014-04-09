package org.wiperdog.netty.test
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executors;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import net.javaforge.netty.servlet.bridge.ServletBridgeChannelPipelineFactory;
import net.javaforge.netty.servlet.bridge.config.ServletConfiguration;
import net.javaforge.netty.servlet.bridge.config.WebappConfiguration;
import net.javaforge.netty.servlet.bridge.impl.ServletBridgeWebapp;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

class NettyServletManager {
	BundleContext context;
	WebappConfiguration webapp;
	ServletBridgeChannelPipelineFactory servletBridge;
	ServerBootstrap bootstrap;
	Channel serverChannel;
	ChannelGroup channelGroup;

	public NettyServletManager(BundleContext context){
		this.context = context;
	}
	public void start() throws Exception {
		println "start server >>>>>>>>>>>>>>>"
		// Configure the server.
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		webapp = new WebappConfiguration();
		servletBridge = new ServletBridgeChannelPipelineFactory(webapp);
		bootstrap.setPipelineFactory(servletBridge);
		serverChannel = bootstrap.bind(new InetSocketAddress(8080));

		new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(3000);
							//Tracking for http servlet service registering
							//If new servlet service registered ,adding to servlet container
							ServiceTracker tracker = new ServiceTracker(context, "javax.servlet.Servlet", new ServletServiceTracker(context));
							tracker.open();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
	}

	public void stop() throws Exception {
		try {
			println "stop server >>>>>>>>>>>>>>>"
			//Stop netty server
			servletBridge.shutdown();
			serverChannel.close().awaitUninterruptibly();
			bootstrap.releaseExternalResources();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	class ServletServiceTracker implements ServiceTrackerCustomizer {
		BundleContext context;

		public ServletServiceTracker(BundleContext context) {
			this.context = context;
		}

		public Object addingService(ServiceReference reference) {
			Object service = context.getService(reference);
			try {
				System.out.println("service: " + service);
				if (service instanceof Servlet) {
					channelGroup = servletBridge.getAllChannels();
					if (webapp.hasServletConfigurations()) {
						// Get list servlet available in servlet container
						// If the old servlet instance is existing ,remove it
						Collection<ServletConfiguration> servlets = webapp.getServlets();
						String servletPath = reference.getProperty("alias").toString();
						if (servlets != null) {
							Iterator<ServletConfiguration> it = servlets.iterator();
							while (it.hasNext()) {
								ServletConfiguration servlet = it.next();
								if (servlet.matchesUrlPattern(servletPath)) {
									it.remove();
									webapp.setServlets(servlets);
								}
							}
						}
					}
					String servletPath = reference.getProperty("alias").toString() + "/";
					HttpServlet servletService = (HttpServlet) service;
					//Adding new servlet
					webapp.addHttpServlet(servletService, servletPath);

					//Init servlet
					ServletBridgeWebapp webServlet = new ServletBridgeWebapp();
					ServletConfiguration servletConfig = new ServletConfiguration(servletService, servletPath);
					WebappConfiguration webappTmp = new WebappConfiguration();
					//Get init param from service properties and set it to servlet configuration
					String[] initParamKeys = reference.getPropertyKeys();
					for (String key : initParamKeys) {
						if (!key.equalsIgnoreCase("alias") && !key.equalsIgnoreCase("service.id") && !key.equalsIgnoreCase("objectClass")) {
							servletConfig.addInitParameter(key, reference.getProperty(key).toString());
						}
					}
					webappTmp.addServletConfigurations(servletConfig);
					webServlet.init(webappTmp, channelGroup);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return service;
		}

		public void modifiedService(ServiceReference reference, Object arg1) {
		}

		public void removedService(ServiceReference arg0, Object arg1) {
		}

	}
}