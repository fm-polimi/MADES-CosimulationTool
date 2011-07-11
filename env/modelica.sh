#!/bin/sh
FOLDER=$1
cd FOLDER

# il nome del modello compilato che
# deve essere presente nella directory in cui si lancia lo script, insieme al file di
# inizializzazione "nomemodello_init.txt"
PROJECT=$2

./$PROJECT

# durante la simulazione verranno monitorate le transizioni e salvate sul file
# A_Transitions (non � detto che avvengano...e che siano presenti)

# Una volta terminata la simulazione verr� prodotto il file contenente i risultati
# nomeModello_res. (csv/mat)

# questo deve essere processato dal programma in python
 python ReadMAT.py ./$PROJECT
 
 # alla fine verr� prodotto il file di output con i risultati della simulazione
 
 
