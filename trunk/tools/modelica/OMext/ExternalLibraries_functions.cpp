#include "ExternalLibraries_functions.h"
extern "C" {

ExternalFunc2_rettype _ExternalFunc2(modelica_real _x)
{
  ExternalFunc2_rettype out;
  state tmp1;
  double _x_ext;
  double _y_ext;
  tmp1 = get_memory_state();
  _x_ext = (double)_x;
  _y_ext = ExternalFunc2(_x_ext);
  out.targ1 = (modelica_real)_y_ext;
  restore_memory_state(tmp1);
  return out;
}



ExternalFunc1_rettype _ExternalFunc1(modelica_real _x)
{
  ExternalFunc1_rettype out;
  state tmp1;
  double _x_ext;
  double _y_ext;
  tmp1 = get_memory_state();
  _x_ext = (double)_x;
  _y_ext = ExternalFunc1_ext(_x_ext);
  out.targ1 = (modelica_real)_y_ext;
  restore_memory_state(tmp1);
  return out;
}


}

