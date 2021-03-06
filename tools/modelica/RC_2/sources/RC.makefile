# Makefile generated by OpenModelica

# Simulations use -O3 by default
SIM_OR_DYNLOAD_OPT_LEVEL=-O3
CC=gcc
CXX=g++
LINK=gcc -shared -export-dynamic
EXEEXT=
DLLEXT=.so
CFLAGS_BASED_ON_INIT_FILE= 
CFLAGS=$(CFLAGS_BASED_ON_INIT_FILE) ${SIM_OR_DYNLOAD_OPT_LEVEL} -falign-functions -march=native -mfpmath=sse ${MODELICAUSERCFLAGS} 
CPPFLAGS=-I"/usr/include/omc" -I.  #include "InitExt.h" #include "PrintExt.h"
LDFLAGS=-L"/usr/lib/omc" -lc_runtime
SENDDATALIBS= -lsendData -lQtNetwork -lQtCore -lQtGui -lrt -lpthread
PERL=perl
MAINFILE=RC.c
MAINOBJ=RC.o

.PHONY: RC
RC: $(MAINOBJ) RC_records.o
	 $(CXX) -I. -o RC$(EXEEXT) $(MAINOBJ) RC_records.o $(CPPFLAGS)  libInitExt.o libPrintExt.o  -lsim -linteractive $(CFLAGS) $(SENDDATALIBS) $(LDFLAGS) -Wl,-Bstatic -lf2c -Wl,-Bdynamic
RC.conv.c: RC.c
	 $(PERL) /usr/share/omc/scripts/convert_lines.pl $< $@.tmp
	 @mv $@.tmp $@
$(MAINOBJ): $(MAINFILE) RC_functions.c RC_functions.h