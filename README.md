NettDemo written in groovy.
==========================

You need to install groovy > 2.x and grape utility. 
To start sample HttpServer with simple servlet, do

        ./prepare_dependency.sh
        groovy NettyServletManager.groovy 

About the preparing dependencies for demo script :

 - Demo script is using some dependencies for running,they will be added to the classpath at runtime by Groovy Grape)
so we need to prepare these dependencies . The work done in ./prepare_dependency.sh
 - With the dependencies is avaiable on maven central repository ,Grape will download and cache them in grape's cache
 - With the dependencies is NOT avaiable on maven central repository (in this script is "netty-servlet-brigde").We need to fetch source project from github and using manven to build and store in maven local repository.Now the grape will using it for script
 - Take care about Grape cache, because this demo uses modified version of netty-servlet-bridge, so if your environment has official version of netty-servlet-bridge already, remove it from your ~/.groovy/grapes directory.
 -

