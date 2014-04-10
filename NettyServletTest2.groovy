// The following '@Grab()' annotation lines are just sample.
// Please rewrite it.
@Grab(group='io.netty', module='netty', version='3.6.2.Final')
@Grab(group='net.javaforge.netty', module='netty-servlet-bridge', version='1.0.0-SNAPSHOT')
@Grab(group='org.slf4j', module='slf4j-log4j12', version='1.7.0')

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import net.javaforge.netty.servlet.bridge.ServletBridgeChannelPipelineFactory;
import net.javaforge.netty.servlet.bridge.config.ServletConfiguration;
import net.javaforge.netty.servlet.bridge.config.WebappConfiguration;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import groovy.lang.GroovyClassLoader;
import groovy.io.FileType;

public class NettyServletHandler {

    public static void main(String[] args) {

    // Configure the server.
        final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        final WebappConfiguration webapp = new WebappConfiguration();
		webapp.addServletConfigurations(new ServletConfiguration(TestServlet.class, "/TestServlet/").addInitParameter("param", "value"));

        // Set up the event pipeline factory.
        final ServletBridgeChannelPipelineFactory servletBridge = new ServletBridgeChannelPipelineFactory(webapp);
        bootstrap.setPipelineFactory(servletBridge);


		//
		// try to add servlet config after server started...
		new Thread(new Runnable () {
				public void run() {
					try {
						Thread.sleep(3000);
						webapp.addServletConfigurations(new ServletConfiguration(TestServlet2.class, "/TestServlet2/").addInitParameter("param", "value"));
					} catch (Exception e) {
					}
				}
			}).start();
		
        // Bind and start to accept incoming connections.
        final Channel serverChannel = bootstrap.bind(new InetSocketAddress(8080));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                servletBridge.shutdown();
                serverChannel.close().awaitUninterruptibly();
                bootstrap.releaseExternalResources();
            }
        });

    }
}

 class TestServlet extends HttpServlet {

	@Override
	public void init() {
		System.out.println("TestServlet init ");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("Go into doGet()")
			String params = this.getInitParameter("param");
			PrintWriter out = response.getWriter();
			out.println("TestServlet init param : " + params);			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void destroy() {
		System.out.println("TestServlet destroyed ");
	}
}

 class TestServlet2 extends HttpServlet {

	@Override
	public void init() {
		System.out.println("TestServlet init ");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("Go into doGet()")
			String params = this.getInitParameter("param");
			PrintWriter out = response.getWriter();
			out.println("TestServlet init param : " + params);			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void destroy() {
		System.out.println("TestServlet destroyed ");
	}
}

