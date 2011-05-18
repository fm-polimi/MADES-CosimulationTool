#!/bin/sh

# il nome del modello compilato che
# deve essere presente nella directory in cui si lancia lo script, insieme al file di
# inizializzazione "nomemodello_init.txt"
nomeModello="testZC"

./$nomeModello

# durante la simulazione verranno monitorate le transizioni e salvate sul file
# A_Transitions (non è detto che avvengano...e che siano presenti)

# Una volta terminata la simulazione verrà prodotto il file contenente i risultati
# nomeModello_res. (csv/mat)

# questo deve essere processato dal programma in python
 python ReadMAT.py ./$nomeModello
 
 # alla fine verrà prodotto il file di output con i risultati della simulazione
 gedit ./$nomeModello"_init.txt" ./$nomeModello"_final.txt" ./A_Transitions 
 
 
