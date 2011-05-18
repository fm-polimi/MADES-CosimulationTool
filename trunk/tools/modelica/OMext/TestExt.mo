model testZC 
 Real x(start=1.0);
 Real y(start=0.0);
 discrete Real s;
equation 
 der(x)=-y;
 der(y)=x;
algorithm
when initial() then
	Init();
end when;
  when x>0 then
    s := 1;
  elsewhen x<=0 then
    s := 0;
  end when;
  
  when change(s) then
  	Print(s,pre(s),time);
  end when;
  
end testZC;

function Print 
  input Real x;
  input Real x_pre;
  input Real t;
external
	Print_ext(x,x_pre,t) annotation(Library="libPrint_ext.o",Include="#include \"Print_ext.h\"");  
end Print;

function Init
external
	Init_ext() annotation(Library="libInit_ext.o",Include="#include \"Print_ext.h\"");
end Init;

