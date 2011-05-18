#ifndef ExternalLibraries__H
#define ExternalLibraries__H
#include "modelica.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "simulation_runtime.h"
extern "C" {
#define ExternalFunc2_rettype_1 targ1
typedef struct ExternalFunc2_rettype_s
{
  modelica_real targ1; /* y */
} ExternalFunc2_rettype;

ExternalFunc2_rettype _ExternalFunc2(modelica_real _x);

extern double ExternalFunc2(double /*_x*/);
#define ExternalFunc1_rettype_1 targ1
typedef struct ExternalFunc1_rettype_s
{
  modelica_real targ1; /* y */
} ExternalFunc1_rettype;

ExternalFunc1_rettype _ExternalFunc1(modelica_real _x);

extern double ExternalFunc1_ext(double /*_x*/);
}
#endif


