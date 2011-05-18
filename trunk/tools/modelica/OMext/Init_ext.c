#include <stdio.h>

void Init_ext(void){
	// file descriptor
	FILE *f;
	
	// Creo il file, se esistente lo cancello
	f = fopen("A_Transitions","w+");
	fclose(f);
	return;
}
