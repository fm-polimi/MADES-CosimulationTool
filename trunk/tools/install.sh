#!/bin/bash
sudo apt-get install default-jre gcc common-lisp-controller sbcl
# TODO(rax): install also open modelica (omc)

# Create a local folder and check out mades
cd ~
mkdir mades
cd mades
# Download the latest JAR 
wget http://code.google.com/p/mades/downloads/detail?name=Mades.jar&can=2&q=
#TODO(rax): make the jar file executable

# Download and install ZOT
# TODO(rax): download it in a tmp folder and remove it after install
wget http://code.google.com/p/mades/downloads/detail?name=zot_25_07_2011.zip&can=2&q=
# TODO(rax): copy the whole zot folder in /usr/local/
# TODO(rax): make a symbolic link from /usr/local/zot/bin/zot to /usr/bin/
# TODO(rax): make sure that /usr/local/zot/bin/zot has the right permission and it is executable!

# TODO(rax): download z3 and place it in /usr/bin
# TODO(rax): WARNING download the right version at 32 or 64 bit!!!!!!!!
# TODO(rax): make sure it can be executed
# solvers
#http://yices.csl.sri.com/download-yices2.shtml
#http://research.microsoft.com/en-us/um/redmond/projects/z3/


# Download and install the examples
# TODO(rax): unzip this file inside the MADES folder. They should be inside a folder called "examples"
wget http://code.google.com/p/mades/downloads/detail?name=examples.zip&can=2&q=
# TODO(rax): remove the file after it has been unzipped
