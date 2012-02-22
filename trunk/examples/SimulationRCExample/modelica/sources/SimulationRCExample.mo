model SimulationRCExample
	/**signals begin**/
	discrete Real c_sig0;
	/**signals end**/

	/**thresholds begin**/
	parameter Real C_Th0 = 6.0;
	/**thresholds end**/

  Real c_volt(stateSelect = StateSelect.always);
  parameter Real in_volt = 10;
  parameter Real R = 10;
  parameter Real C = 0.5;
  Modelica.Electrical.Analog.Basic.Resistor R1(R = R) annotation(Placement(visible = true, transformation(origin = {-31.7626,24.8034}, extent = {{-12,-12},{12,12}}, rotation = 0)));
  Modelica.Electrical.Analog.Sources.SignalVoltage E annotation(Placement(visible = true, transformation(origin = {-55.6737,0.356883}, extent = {{-12,-12},{12,12}}, rotation = 0)));
  Modelica.Electrical.Analog.Basic.Capacitor C1(C = C) annotation(Placement(visible = true, transformation(origin = {4.81792,25.3387}, extent = {{-12,-12},{12,12}}, rotation = 0)));
  Modelica.Electrical.Analog.Basic.Ground G annotation(Placement(visible = true, transformation(origin = {-7.13766,-17.8442}, extent = {{-12,-12},{12,12}}, rotation = 0)));
equation
  connect(E.n,G.p) annotation(Line(points = {{-43.6737,0.356883},{-6.78078,0.356883},{-6.78078,-5.84415},{-7.13766,-5.84415}}));
  connect(C1.n,G.p) annotation(Line(points = {{16.8179,25.3387},{20.3423,25.3387},{20.3423,-5.35325},{-7.13766,-5.35325},{-7.13766,-5.84415}}));
  connect(R1.n,C1.p) annotation(Line(points = {{-19.7626,24.8034},{-6.60234,24.8034},{-6.60234,25.3387},{-7.18208,25.3387}}));
  connect(E.p,R1.p) annotation(Line(points = {{-67.6737,0.356883},{-79.4065,0.356883},{-79.4065,24.6249},{-43.7626,24.6249},{-43.7626,24.8034}}));
  E.v = in_volt;
  c_volt = C1.v;
algorithm
  when initial() then
      Init();
  
  end when;

	/**triggers begin**/
	when c_volt > C_Th0 then
		c_sig0 := 1.0;
	elsewhen c_volt <= C_Th0 then
		c_sig0 := 0.0;
	end when;

	when change(c_sig0) then
		FilePrint("c_volt", c_sig0, pre(c_sig0), time);
	end when;

	/**triggers end**/
end SimulationRCExample;
/** include begin **/
function Init
external
	InitExt() annotation(Library="libInitExt.o",Include="#include \"InitExt.h\"");
end Init;

function FilePrint
	input String varName;
	input Real x;
	input Real x_pre;
	input Real t;
external
	PrintExt(varName,x,x_pre,t) annotation(Library="libPrintExt.o",Include="#include \"PrintExt.h\"");
end FilePrint;
/** include end **/


