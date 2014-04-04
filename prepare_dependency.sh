#!/bin/sh
# 1. If you don't want to describe the dependencies as the form of "@Grab()"
#  in the groovy source file, please write required dependencies in this
#   script as 'grape' command invokation.
# 2. If you need some additional preparation, please script it here.

which grape > /dev/null 2>&1 || (echo "No usable grape command installed";exit 1)

grape install io.netty netty 3.9.0.Final

which git > /dev/null 2>&1 || (echo "No git command installed";exit 1)

if [ ! -d netty-servlet-bridge ];then
  git clone https://github.com/bigpuritz/netty-servlet-bridge.git
fi
sleep 3s
which mvn > /dev/null 2>&1 || (echo "No maven command installed";exit 1)

cd netty-servlet-bridge/netty-servlet-bridge && mvn clean install

grape install net.javaforge.netty netty-servlet-bridge 1.0.0-SNAPSHOT


