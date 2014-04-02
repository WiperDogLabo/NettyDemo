// The following '@Grab()' annotation lines are just sample.
// Please rewrite it.
@Grab(group='io.netty', module='netty', version='3.9.0.Final')
// @Grab(....)
// @Grab(....)
// @Grab(....)
// @Grab(....)

import net.javaforge.netty.servlet.bridge.ServletBridgeChannelPipelineFactory;
import net.javaforge.netty.servlet.bridge.config.ServletConfiguration;
import net.javaforge.netty.servlet.bridge.config.WebappConfiguration;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import groovy.lang.GroovyClassLoader;
import groovy.io.FileType;

public class NettyServletHandler {

    private static final Logger log = LoggerFactory.getLogger(NettyServletHandler.class);
    private static final properties = MonitorJobConfigLoader.getProperties();
    public static final String NETTY_PORT = "netty.port";

    public static void main(String[] args) {

//// Commented out the servlet auto deploying code, because...
//// Just for make this sample programe keep in simple,
////  please write code of HttpServlet implementation class into this file.
//
//        // Path to folder contains servlet
//        def dirServlet = new File(properties.get(ResourceConstants.SERVLET_DIRECTORY))
//
//        // Configure the server.
//        final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
//            Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
//
//        // Registering servlet in servlet container
//        WebappConfiguration webapp = new WebappConfiguration();
//        if (dirServlet.exists()) {
//            dirServlet.eachFileRecurse(FileType.FILES) { file ->
//                def servletClass = new GroovyClassLoader().parseClass(new File(file.getAbsolutePath()))
//                webapp.addServletConfigurations(new ServletConfiguration(servletClass, "/" + servletClass.getName() + "/*"));
//            }
//        }
        // Set up the event pipeline factory.
        final ServletBridgeChannelPipelineFactory servletBridge = new ServletBridgeChannelPipelineFactory(webapp);
        bootstrap.setPipelineFactory(servletBridge);

        // Bind and start to accept incoming connections.
        def port = Integer.parseInt(System.getProperty(NETTY_PORT));
        final Channel serverChannel = bootstrap.bind(new InetSocketAddress(port));

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
