# Makefile generated by OpenModelica

# Simulations use -O3 by default
SIM_OR_DYNLOAD_OPT_LEVEL=-O3
CC=g++
CXX=g++
LINK=g++ -shared -export-dynamic
EXEEXT=
DLLEXT=.so
CFLAGS_BASED_ON_INIT_FILE= 
CFLAGS=$(CFLAGS_BASED_ON_INIT_FILE) -I"/usr/include/omc" ${SIM_OR_DYNLOAD_OPT_LEVEL} -falign-functions -march=native -mfpmath=sse ${MODELICAUSERCFLAGS} 
LDFLAGS=-L"/usr/lib/omc" -lc_runtime
SENDDATALIBS= -lsendData -lQtNetwork -lQtCore -lQtGui -lrt -lpthread
PERL=perl
MAINFILE=ExternalLibraries.cpp

.PHONY: ExternalLibraries
ExternalLibraries: $(MAINFILE) ExternalLibraries_functions.cpp ExternalLibraries_functions.h ExternalLibraries_records.c
	 $(CXX) -I. -o ExternalLibraries$(EXEEXT) $(MAINFILE)  libExternalFunc2.a libExternalFunc1_ext.o  -lsim -linteractive $(CFLAGS) $(SENDDATALIBS) $(LDFLAGS) -Wl,-Bstatic -lf2c -Wl,-Bdynamic ExternalLibraries_records.c
ExternalLibraries.conv.cpp: ExternalLibraries.cpp
	 $(PERL) /usr/share/omc/scripts/convert_lines.pl $< $@.tmp
	 @mv $@.tmp $@