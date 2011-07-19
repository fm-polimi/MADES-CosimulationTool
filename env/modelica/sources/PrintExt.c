#include <stdio.h>
#include <stddef.h>
#include <string.h>
#include <stdlib.h>

void PrintExt(double x,double x_pre,double t){
	// file descriptor
	FILE *f;
	char time[100];
	
	
	// apriamo il file a in lettura
	f = fopen("A_Transitions","a");
	
	if (f!=NULL){
		fseek(f,0,SEEK_END);
		
		if (x > x_pre){
			sprintf(time,"TRANSnp:\tCOND1\t%.6f\n",t);
			fputs(time,f);
		}else{
			sprintf(time,"TRANSpn:\tCOND1\t%.6f\n",t);
			fputs(time,f);
		}
	}
	
	fclose(f);
	return;
}
