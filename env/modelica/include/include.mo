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
