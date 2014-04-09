import javax.servlet.ServletContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.osgi.framework.ServiceRegistration
public class TestServlet3 extends HttpServlet {
	def localVar;
	@Override
	public void init() {
		System.out.println("TestServlet3 init ");
		localVar = 10
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			out.println("TestServlet3 loaded ok 11!" + localVar);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
	}

	@Override
	public void destroy() {
		System.out.println("TestServlet3 destroyed ");
	}
}

def servletRoot
try {
	servletRoot = new TestServlet3()
} catch (e) {
}


if (servletRoot != null) {
	def props = new java.util.Hashtable()
	props.put "alias", "/TestServlet3"
	props.put "initParam1", "initParam1 of TestServlet3"
	props.put "initParam2", "initParam2 of TestServlet3"

	// safeRegisterService method is customized version of BundleContext.registerService()
	ctx.registerService("javax.servlet.Servlet", servletRoot, props)

}