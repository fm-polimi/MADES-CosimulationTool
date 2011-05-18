Il file TextExt.mo contiene i modelli e le funzioni modelica che permettono l'interfacciamento. Le funzioni esterne che vengono utilizzate
sono 2

Print - scrive i dati su un file quando avvengono transizioni
Init - crea il file su cui inserire le transizioni e se esistente lo cancella creandone uno nuovo

Il file .mos (modelica script) permetta la creazione del compilato...a cui possiamo passare i parametri della simulazione che si trovano
in un bellissimo file .txt

alla fine della simulazione viene restituito il file contenene le transizioni ed il csv

Il file csv viene processato da python che legge direttamente i noi delle variabili in gioco ed i loro valori finali

------
creare un file txt che contenga i valori finali delle variabili inizialmente letti! 
