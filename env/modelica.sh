#!/bin/sh
FOLDER=$1
CURRENT_DIR=${PWD}
BASEDIR=$(dirname $0)

# il nome del modello compilato che
# deve essere presente nella directory in cui si lancia lo script, insieme al file di
# inizializzazione "nomemodello_init.txt"
PROJECT=$2

cd $FOLDER
./$PROJECT

# durante la simulazione verranno monitorate le transizioni e salvate sul file
# A_Transitions (non è detto che avvengano...e che siano presenti)

# Una volta terminata la simulazione verrà prodotto il file contenente i risultati
# nomeModello_res. (csv/mat)

# questo deve essere processato dal programma in python
cd $CURRENT_DIR
python $BASEDIR/ReadMAT.py $FOLDER/$PROJECT
 
 # alla fine verrà prodotto il file di output con i risultati della simulazione
 
 
