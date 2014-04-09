import javax.servlet.ServletContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.osgi.framework.ServiceRegistration
public class TestServlet2 extends HttpServlet {
	public localVar = null;
	@Override
	public void init() {
		System.out.println("TestServlet2 init ");
		localVar = "TestServlet2"
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			out.println("TestServlet2 loaded ok:");
			out.println("TestServlet2 instance variable : " + localVar);
			out.println("TestServlet2 init params : ");
			this.getInitParameterNames().each {				
				out.println (it + ":" + this.getInitParameter(it))
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
	}

	@Override
	public void destroy() {
		System.out.println("TestServlet2 destroyed " +  this.getInitParameter("param"));
	}
}

def servletRoot
try {
	servletRoot = new TestServlet2()
} catch (e) {
}


if (servletRoot != null) {
	def props = new java.util.Hashtable()
	props.put "alias", "/TestServlet2"
	props.put "initParam1", "initParam1 of TestServlet2"
	props.put "initParam2", "initParam2 of TestServlet2"

	// safeRegisterService method is customized version of BundleContext.registerService()

	ctx.registerService("javax.servlet.Servlet", servletRoot, props)

}