#include "Init.h"
#include <algorithm>
#define MODELICA_ASSERT(info,msg) { printInfo(stderr,info); fprintf(stderr,"Modelica Assert: %s!\n", msg); }
#define MODELICA_TERMINATE(msg) { fprintf(stderr,"Modelica Terminate: %s!\n", msg); fflush(stderr); }

extern "C" {


void _Init()
{
  state tmp1;
  tmp1 = get_memory_state();
  Init_ext();
  restore_memory_state(tmp1);
  return ;
}

int in_Init(type_description * inArgs, type_description * outVar)
{
  void out;
  MMC_TRY_TOP()
  out = _Init();
  MMC_CATCH_TOP(return 1)
  return 0;
}

}

