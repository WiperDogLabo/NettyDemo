// The following '@Grab()' annotation lines are just sample.
// Please rewrite it.
@Grab(group='org.jboss.netty', module='netty', version='3.2.6.Final')
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
import net.javaforge.netty.servlet.bridge.impl.ServletBridgeWebapp;
import org.jboss.netty.channel.group.ChannelGroup;

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
		//webapp.addServletConfigurations(new ServletConfiguration(TestServlet.class, "/TestServlet/").addInitParameter("param", "value"));
		
		//Adding servlet berfore server start
		def testServlet = new TestServlet()
		String servletPath = testServlet.getServletPath() + "/";
		ServletConfiguration servletConfig = new ServletConfiguration(testServlet, servletPath);
		servletConfig.addInitParameter("quantity", "1");
		webapp.addServletConfigurations(servletConfig);

        // Set up the event pipeline factory.
        final ServletBridgeChannelPipelineFactory servletBridge = new ServletBridgeChannelPipelineFactory(webapp);
        bootstrap.setPipelineFactory(servletBridge);

        // Bind and start to accept incoming connections.
        final Channel serverChannel = bootstrap.bind(new InetSocketAddress(8080));
		
		// try to add servlet config after server started...
		new Thread(new Runnable () {
				public void run() {
					try {
						Thread.sleep(3000);
						//webapp.addServletConfigurations(new ServletConfiguration(TestServlet2.class, "/TestServlet2/").addInitParameter("param", "value"));
						ChannelGroup channelGroup = servletBridge.getAllChannels();
						//Create servlet instances
						def testServlet1 = new TestServlet1()
						def testServlet2 = new TestServlet2()

						//Get servlet path
						String servletPath1 = testServlet1.getServletPath() + "/";
						String servletPath2 = testServlet2.getServletPath() + "/";

						//Adding new servlet
						webapp.addHttpServlet(testServlet1, servletPath1);
						webapp.addHttpServlet(testServlet2, servletPath2);

						//Init for servlet instances

						
						WebappConfiguration webappTmp = new WebappConfiguration();						
						ServletBridgeWebapp webServlet = new ServletBridgeWebapp();
						ServletConfiguration servletConfig1 = new ServletConfiguration(testServlet1, servletPath1);
						
						//Set init param to servlets
						
						//Init testServlet1
						servletConfig1.addInitParameter("quantity", "2");
						webappTmp.addServletConfigurations(servletConfig1);						
						webServlet.init(webappTmp, channelGroup);

						//Init testServlet2
						webappTmp = new WebappConfiguration();
						servletConfig1 = new ServletConfiguration(testServlet2, servletPath2);
						servletConfig1.addInitParameter("quantity", "3");
						webappTmp.addServletConfigurations(servletConfig1);						
						webServlet.init(webappTmp, channelGroup);


					} catch (Exception e) {
						e.printStackTrace()
					}
				}
			}).start();
		
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
 	String servletPath = "/TestServlet"
	def localVar 

	@Override
	public void init() {
		System.out.println("TestServlet init ");
		localVar = 10
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("Go into TestServlet's doGet()")
			String quantity = this.getInitParameter("quantity");
			PrintWriter out = response.getWriter();
			out.println("TestServlet init quantity : " + quantity);			
			out.println("TestServlet local variable : " + localVar);
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

	public String getServletPath(){
		return this.servletPath
	}
}

 class TestServlet1 extends HttpServlet {
 	String servletPath = "/TestServlet1"
	def localVar 

	@Override
	public void init() {
		System.out.println("TestServlet1 init ");
		localVar = 20
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("Go into TestServlet1's doGet()")
			String quantity = this.getInitParameter("quantity");
			PrintWriter out = response.getWriter();
			out.println("TestServlet1 init quantity : " + quantity);			
			out.println("TestServlet1 local variable : " + localVar);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void destroy() {
		System.out.println("TestServlet1 destroyed ");
	}

	public String getServletPath(){
		return this.servletPath
	}
}

 class TestServlet2 extends HttpServlet {
	String servletPath = "/TestServlet2"
	def localVar
	@Override
	public void init() {
		System.out.println("TestServlet2 init ");
		localVar = 30
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("Go into TestServlet2's doGet()")
			String quantity = this.getInitParameter("quantity");
			PrintWriter out = response.getWriter();
			out.println("TestServlet2 init quantity : " + quantity);			
			out.println("TestServlet2 local variable : " + localVar);
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

	public String getServletPath(){
		return this.servletPath
	}

}


