#!/bin/sh
# If you don't want to describe the dependencies as the form of "@Grab()"
#  in the groovy source file, please write required dependencies in this
#   script as 'grape' command invokation.
#

which grape > /dev/null 2>&1 || (echo "No usable grape command installed";exit 1)

grape install io.netty netty 3.9.0.Final

