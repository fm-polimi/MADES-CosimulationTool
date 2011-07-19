#include <stdio.h>
#include <stddef.h>
#include <string.h>

void InitExt(void){
	// file descriptor
	FILE *f;
	char riga[50];
	
	// Creo il file, se esistente lo cancello
	f = fopen("A_Transitions","w+");
	sprintf(riga, "= File delle transizioni =\n");
	fputs(riga,f);
	fclose(f);
	return;
}
