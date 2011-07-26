#!/bin/bash
VERSION=`uname -m`

#Verify if ~/mades_r1 folder exists, if true please check the content before continue
if [ -d ~/mades_r1 ]
	then
    	echo "Mades directory already exists, please verify"
        exit 1
    else
		if grep 'deb http://build.openmodelica.org/apt nightly-ubuntu contrib' /etc/apt/sources.list
			then
        		echo "sources.list entry exists"
			else
        		echo "sources.list does not exists"
        		echo "deb http://build.openmodelica.org/apt nightly-ubuntu contrib" | sudo tee --append /etc/apt/sources.list
		fi

		wget -q http://build.openmodelica.org/apt/openmodelica.asc -O- | sudo apt-key add - 

		sudo apt-get update
		sudo apt-get install default-jre gcc common-lisp-controller sbcl omc openmodelica

		# Create a local folder and check out mades
		cd ~
		mkdir mades_r1
		cd mades_r1
		mkdir zot_archive

		# Download the latest JAR 
		wget http://mades.googlecode.com/files/mades_all.tar.gz
		tar zxvf mades_all.tar.gz
		sudo chmod +x ./mades/Mades.jar

		# Download and install ZOT
		wget http://mades.googlecode.com/files/zot_25_07_2011.tar.gz
		tar zxvf zot_25_07_2011.tar.gz
		if [ -d /usr/local/zot ]
			then
        		echo "Zot directory exists"
        		tar zcvf ~/mades_r1/zot_archive/zot-`date +\%Y-\%m-\%d-\%s`.sql.gz /usr/local/zot
        		sudo rm -R /usr/local/zot
			else 
        		echo "Zot directory does not exists"
		fi
		sudo mv zot /usr/local
		sudo ln -s /usr/local/zot/bin/zot /usr/bin
		sudo chmod +x /usr/local/zot/bin/zot

		if VERSION="x86_64"
			then
				wget http://research.microsoft.com/projects/z3/z3-x64-2.19.tar.gz
				tar zxvf z3-x64-2.19.tar.gz
			else
				wget http://research.microsoft.com/projects/z3/z3-2.19.tar.gz
				tar zxvf z3-2.19.tar.gz
		fi
		sudo ln -s -f ~/mades_r1/z3/bin/z3 /usr/bin

# solvers
#http://yices.csl.sri.com/download-yices2.shtml
#http://research.microsoft.com/en-us/um/redmond/projects/z3/

fi