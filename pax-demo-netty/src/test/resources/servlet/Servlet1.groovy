import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.osgi.framework.ServiceRegistration
public class TestServlet1 extends HttpServlet {
	public localVar = null;
	
	@Override
	public void init() {
		System.out.println("TestServlet1 init ");
		localVar = 1
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			out.println("TestServlet1 loaded ok !");
			out.println("TestServlet1 instance variable: " + localVar);
			out.println("TestServlet1 init params : ");
			this.getInitParameterNames().each {
				out.println (this.getInitParameter(it))
			}
			//out.println (this.getInitParameterNames())
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
}

def servletRoot
try {
	servletRoot = new TestServlet1()
} catch (e) {
}


if (servletRoot != null) {
	def props = new java.util.Hashtable()
	props.put "alias", "/TestServlet1"
	props.put "initParam1", "initParam1 of TestServlet1"
	props.put "initParam2", "initParam2 of TestServlet1"
	// safeRegisterService method is customized version of BundleContext.registerService()

	ctx.registerService("javax.servlet.Servlet", servletRoot, props)

}