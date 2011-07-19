#ifndef RC__H
#define RC__H
#include "modelica.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "simulation_runtime.h"
#ifdef __cplusplus
extern "C" {
#endif

void _Init();

extern void InitExt();

void _FilePrint(modelica_real _x, modelica_real _x_pre, modelica_real _t);

extern void PrintExt(double /*_x*/, double /*_x_pre*/, double /*_t*/);
#ifdef __cplusplus
}
#endif
#endif


