model RC
	Modelica.Electrical.Analog.Basic.Resistor R1(R = R);
	Modelica.Electrical.Analog.Basic.Capacitor C1(C = C);
	Modelica.Electrical.Analog.Basic.Ground G;
	Modelica.Electrical.Analog.Sources.SignalVoltage E;
	Real COND1(stateSelect=StateSelect.always);
	parameter Real threshold_C1_v = 6;
	parameter Real REACT1 = 10;
	parameter Real R = 10;
	parameter Real C = 0.1;
	discrete  Real trigger_C1_v;
equation
	// tensione di alimentazione -- ingresso
	E.v = REACT1;
	
	// connessione dei componenti
	connect(E.p,R1.p);
	connect(R1.n,C1.p);
	connect(C1.n,G.p);
	connect(G.p,E.n);
	
	// tensione sul condensatore -- uscita
	COND1 = C1.v;
	
algorithm
	
	when initial() then
		Init();
	end when;
	
	when C1.v > threshold_C1_v then
		trigger_C1_v := 1.0;
	elsewhen C1.v <= threshold_C1_v then
		trigger_C1_v := 0.0;
	end when;
	 
  	when change(trigger_C1_v) then
  	  	FilePrint(trigger_C1_v, pre(trigger_C1_v), time);
  	end when;
  	
  	
end RC;

function Init
external
	InitExt() annotation(Library="libInitExt.o",Include="#include \"InitExt.h\"");
end Init;

function FilePrint
	input Real x;
	input Real x_pre;
	input Real t;
external
	PrintExt(x,x_pre,t) annotation(Library="libPrintExt.o",Include="#include \"PrintExt.h\"");
end FilePrint;

