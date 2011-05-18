#include "testZC_functions.h"
extern "C" {

void _Init()
{
  state tmp1;
  tmp1 = get_memory_state();
  Init_ext();
  restore_memory_state(tmp1);
  return ;
}



void _Print(modelica_real _x, modelica_real _x_pre, modelica_real _t)
{
  state tmp1;
  double _x_ext;
  double _x_pre_ext;
  double _t_ext;
  tmp1 = get_memory_state();
  _x_ext = (double)_x;
  _x_pre_ext = (double)_x_pre;
  _t_ext = (double)_t;
  Print_ext(_x_ext, _x_pre_ext, _t_ext);
  restore_memory_state(tmp1);
  return ;
}


}

