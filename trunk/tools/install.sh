#!/bin/bash
sudo apt-get install default-jre gcc common-lisp-controller sbcl
# TODO(rax): install also open modelica (omc)

# Create a local folder and check out mades
cd ~
mkdir mades
cd mades
# Download the latest JAR 
wget http://mades.googlecode.com/files/mades_all.tar.gz

#TODO(rax): make the jar file executable

# Download and install ZOT
# TODO(rax): download it in a tmp folder and remove it after install
wget http://mades.googlecode.com/files/zot_25_07_2011.zip
# TODO(rax): copy the whole zot folder in /usr/local/
# TODO(rax): make a symbolic link from /usr/local/zot/bin/zot to /usr/bin/
# TODO(rax): make sure that /usr/local/zot/bin/zot has the right permission and it is executable!

# TODO(rax): download z3 and place it in /usr/bin
# TODO(rax): WARNING download the right version at 32 or 64 bit!!!!!!!!
# TODO(rax): make sure it can be executed
# solvers
#http://yices.csl.sri.com/download-yices2.shtml
#http://research.microsoft.com/en-us/um/redmond/projects/z3/

