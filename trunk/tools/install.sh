#!/bin/bash
apt-get install default-jre python gcc clisp common-lisp-controller subversion python-pip python-dev python-scipy
cd ~
mkdir mades
cd mades
svn checkout http://mades.googlecode.com/svn/trunk/ repo
wget http://home.dei.polimi.it/pradella/Zot/zot.tar.gz
tar zxvf zot.tar.gz
cd zot
sudo ln -s ~/mades/zot/minisat /usr/bin/
sudo ln -s ~/mades/zot/*.asd /usr/share/common-lisp/systems/




