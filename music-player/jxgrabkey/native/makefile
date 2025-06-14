# https://makefiletutorial.com/

###################################################################################################
# Makefiles must be indented using TABs and not spaces or make will fail!
###################################################################################################

#The important variables used by implicit rules are:
#    CC: Program for compiling C programs; default cc
#    CXX: Program for compiling C++ programs; default g++
#    CFLAGS: Extra flags to give to the C compiler
#    CXXFLAGS: Extra flags to give to the C++ compiler
#    CPPFLAGS: Extra flags to give to the C preprocessor
#    LDFLAGS: Extra flags to give to compilers when they are supposed to invoke the linker

# Environment
SHELL=/bin/bash

MKDIR = mkdir
#CC = gcc # Flag for implicit rules
#CFLAGS = -g # Flag for implicit rules. Turn on debug info
CFLAGS=-m64
CXXFLAGS=-m64
LDFLAGS=-m64

# ${MAKE} FILE TARGET

all: clean build

clean:
	echo $@ # echo printed itself
	
	rm -rf ../build/native
	${MKDIR} -p ../build/native

build: 
	@echo $@ # only build is printed

	$(CXX) -shared -fPIC -lX11 JXGrabKey.cpp -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux -o ../build/native/JXGrabKey.so
