#include <stdio.h>
#include <stddef.h>
#include <string.h>
#include <stdlib.h>

void PrintExt(const char* varName, double x, double x_pre, double t) {
	// file descriptor
	FILE *f;
	char time[100];
	
	f = fopen("Transitions", "a");
	
	if (f != NULL){
		fseek(f, 0, SEEK_END);
		
		if (x > x_pre){
			sprintf(time, "TRANSnp:\t%s\t%.6f\n", varName, t);
			fputs(time, f);
		} else {
			sprintf(time, "TRANSpn:\t%s\t%.6f\n", varName, t);
			fputs(time, f);
		}
	}
	
	fclose(f);
	return;
}
