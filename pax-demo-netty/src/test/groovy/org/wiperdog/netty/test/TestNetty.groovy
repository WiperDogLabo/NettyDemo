package org.wiperdog.netty.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.junit.runner.JUnitCore;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.wiperdog.directorywatcher.Listener;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TestNetty {
	public TestNetty() {
	}

	@Inject
	private org.osgi.framework.BundleContext context;
	@Configuration
	public Option[] config() {

		return options(
		cleanCaches(true),
		frameworkStartLevel(6),
		// felix log level
		systemProperty("felix.log.level").value("4"), // 4 = DEBUG
		// setup properties for fileinstall bundle.
		systemProperty("felix.home").value(System.getProperty("user.dir")),
		// Pax-exam make this test code into OSGi bundle at runtime, so
		// we need "groovy-all" bundle to use this groovy test code.
		mavenBundle("org.codehaus.groovy", "groovy-all", "2.2.1").startLevel(2),
		//mavenBundleWrapper("org.codehaus.groovy", "groovy-all", "2.2.1").startLevel(2),,
		wrappedBundle(mavenBundle("javax.servlet", "servlet-api", "2.5")),
		wrappedBundle(mavenBundle("io.netty", "netty", "3.6.2.Final")),
		wrappedBundle(mavenBundle("net.javaforge.netty", "netty-servlet-bridge", "1.0.0-SNAPSHOT")),
		mavenBundle("org.wiperdog", "org.wiperdog.directorywatcher", "0.1.0").startLevel(2),
		junitBundles()
		);
	}
	NettyServletManager nettyServer;
	@Before
	public void prepare() {
		nettyServer = new NettyServletManager(context)
		nettyServer.start()
	}

	@After
	public void finish() {
		nettyServer.stop()
	}
	
	@Test
	public void TestNettyServletManager() {
		//The demo using directory watcher to 
		//watching for groovy file and load if new servlet file added or servlet modified
		def binding = new Binding();
		ServiceRegistration srg ;
		binding.setVariable("ctx", context);
		binding.setVariable("srg", srg);
		def groovyDir = "src/test/resources/servlet";
		def shell = new GroovyShell(this.getClass().getClassLoader(),binding)
		def servletListener = new ServletListener(groovyDir, shell);
		context.registerService(Listener.class.getName(),servletListener,null);
		//Waiting for server boot up and servlet loaded
		Thread.sleep(7000)
		
		//Now using curl command or web browser to send some request to servlet
		//Example : curl localhost:8080/TestServlet1
		//To interrupt the netty server listen on port 8080 using command : kill -9 `lsof -t -i:8080`
		//You can add or modified a servlet file in folder : src/test/resources/servlet
	
		def listCmd = new ArrayList<String>();
		listCmd.add("curl")
		listCmd.add("-v")
		listCmd.add("localhost:8080/TestServlet1")
		println this.sentRequest(listCmd)
		Thread.sleep(1000000);
	}
	/**
	 * Sent request to server and read return data
	 */
	public String sentRequest(listCmd){
		ProcessBuilder pb = new ProcessBuilder(listCmd);
		pb.redirectErrorStream(true);
		Process proc = pb.start();
		BufferedReader bf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = null;
		String result = ""
		while ((line = bf.readLine()) != null) {
			result += line + "\n";
		}
		return result
	}

}
