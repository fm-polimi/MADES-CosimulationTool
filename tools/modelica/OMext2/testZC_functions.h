#ifndef testZC__H
#define testZC__H
#include "modelica.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "simulation_runtime.h"
extern "C" {

void _Init();

extern void Init_ext();

void _Print(modelica_real _x, modelica_real _x_pre, modelica_real _t);

extern void Print_ext(double /*_x*/, double /*_x_pre*/, double /*_t*/);
}
#endif


