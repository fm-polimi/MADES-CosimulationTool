import csv
import re
import string

# aprire il file csv
reader = csv.reader(file("./testZC_res.csv", "rb"))

j = 0
name = []
val = []

# iniziare la lettura delle righe
for row in reader:
	j = j+1
	i = 0
	# la prima riga contiene i nomie
	if j==1:
		for el in row:
			name.append(el)
			
	# verifico che sia l'ultima riga
	try:
		reader.next()
	except StopIteration:
		for el in row:
			val.append(el) 	
			
# i nomi delle variabili si trovano nel vettore name
# i valori si trovano nel vellore val

# apro il file di input
fid = open("./testZC_init.txt","r")
# leggo le linee
lines = fid.readlines()

# espressione regolare per i parametri del modello
p_m = re.compile('([^ ]*)((\s[/][/])([^ ^\n]*))((\s[/][/])([^ ^\n]*))*')
# espressione regolare per i parametri della simulazione
p_sim = re.compile('([^ ]*)(\s[/][/])([^\n]*)')

# apro il file su cui andro' a scrivere i valori finali
fo = open("./testZC_final.txt","w")

# indice su cui iterare per la ricerca dei nomi delle variabili
# parto da 1 perche l'elemento zero e' sempre time!
# gli altri vanno tutti in fila
# se mancasse la corrispondenza fra file di inizializzazione e csv bisogna spendere del tempo a cercarli
j = 1
for stringa in lines:
	# per ogni linea del file di inizializzazione
		
	# controllo la linea con una espressione regolare
	m = p_m.match(stringa)
	# se viene riconosciuto qualcosa allora proseguo
	if m!=None:
		# divido la linea in campi attraverso l'espressione regolare di cui sopra
		campi = m.groups()
		# se il primo delimitatore e' composto solo dalla stringa ' //'
		# allora significa che la linea non riguarda i parametri del modello ma
		# i parametri di simulazione, deve essere parsata con un'altra ER
		if campi[1]==' //':
			m = p_sim.match(stringa)
			campi = m.groups()
			# posso analizzare i campi della simulazione
			
			# cerco il campo start time, se lo trovo
			if campi[2]==' start value':
				# cerco il valore dell'istante finale della simulazione
				new_start_time = val[name.index("time")]
				toFile = new_start_time+campi[1]+campi[2]+"\n"
			else:
				toFile = campi[0]+campi[1]+campi[2]+"\n"
			# riscrivo sul file di output
			# ricordarsi di modificare t start
			
			fo.write(toFile)
		else:
			# posso analizzare i parametri, valori iniziali del modello
			# controllando se c'e' un corrispettivo nel .csv letto prima ed eventualmente
			# cambiandone il valore
			
			
			if campi[3]==name[j] or campi[6]==name[j]:
				# riscrivo sul file di output
				if campi[4]!=None:
					toFile = val[j]+campi[1]+campi[4]+"\n"
				else:
					toFile = val[j]+campi[1]+"\n"
				j = j+1
				
			fo.write(toFile)
	else:
		print "Errore - linear del file init.txt non riconoscibile"
				
# suppongo che le variabili siano ordinate nello stesso modo sia nel file csv che nel file txt
fid.close()
fo.close()
    



