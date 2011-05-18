model Test
	Real x(start = x_start);
	parameter Real a = -1;
	parameter Real x_start = 4;
equation
	der(x) = a*x;
end Test;
