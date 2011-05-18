#include <stdio.h>
#include <stddef.h>
#include <string.h>

void Print_ext(double x,double x_pre,double t){
	// file descriptor
	FILE *f;
	char time[20];
	
	// apriamo il file a in lettura
	f = fopen("A_Transitions","a");
	if (f!=NULL){
		fseek(f,0,SEEK_END);
	
		if (x>0 && x_pre<=0){
			sprintf(time, "Trans:\tnp\t%.6f\n",t);
			fputs(time,f);
		}else{
			sprintf(time, "Trans:\tpn\t%.6f\n",t);
			fputs(time,f);
		}
	}
	fclose(f);
	return;
}
