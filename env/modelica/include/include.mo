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
