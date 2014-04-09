This is a copy of this repository :
https://github.com/bigpuritz/netty-servlet-bridge/tree/master/netty-servlet-bridge

To using for our purpose in wiperdog , need to change a litle of source code :
 Details: 
 - net/javaforge/netty/servlet/bridge/impl/HttpServletRequestImpl.java
   + setCookie() : line 97 => check null before set
 - net/javaforge/netty/servlet/bridge/config/WebappConfiguration.java
   + generate getters ,setter for  (private Collection<ServletConfiguration> servlets;)
 - net/javaforge/netty/servlet/bridge/impl/ServletBridgeWebapp.java 
   + line 44 : change default constructor(constructor with no parameters) visibility to public 

