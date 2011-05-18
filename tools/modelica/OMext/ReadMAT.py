import scipy.io
import csv
import re
import string
import sys

def Main():
	
	# apro il file di input
	fid = open(ModelName+"_init.txt","r")
	# leggo le linee
	lines = fid.readlines()

	# espressione regolare per i parametri del modello
	p_m = re.compile('([^ ]*)((\s[/][/])([^ ^\n]*))((\s[/][/])([^ ^\n]*))*')
	# espressione regolare per i parametri della simulazione
	p_sim = re.compile('([^ ]*)(\s[/][/])([^\n]*)')

	# apro il file su cui andro' a scrivere i valori finali
	fo = open(ModelName+"_final.txt","w")

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
					# la variabile time e' la numero zero - SEMPRE -
					new_start_time = str(cerca_valore(0,time_steps))
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
				
				# CONTROLLO SUL NOME!!!
				# se non e' quello corretto vai avanti, prima o poi si trovera' quello corretto??!!
				while campi[4]!=" //"+names[j] and campi[1]!=" //"+names[j]:
					if j==len(names[:])-1:
						break
					j = j+1	
					
				# riscrivo sul file di output
				val_str = str(cerca_valore(j,time_steps))
				if campi[4]!=None:
					toFile = val_str +campi[1]+campi[4]+"\n"
				else:
					toFile = val_str +campi[1]+"\n"
				j = j+1
					
				fo.write(toFile)
		else:
			print "Errore - linear del file init.txt non riconoscibile"
					
	# suppongo che le variabili siano ordinate nello stesso modo sia nel file csv che nel file txt
	fid.close()
	fo.close()

# se viene richiesta la variabile i-esima
# sapendo che l'ordine DEVE essere lo stesso del file di inizializzazione ***.txt

def cerca_valore(var,time):
	table = dataInfo[0][var]
	index = abs(dataInfo[1][var])-1 # this -1 is because the matlab notation starts with i = 1,...

	if dataInfo[1][var]>= 0:
		sign = 1
	else:
		sign = -1

	if table==1 :
		data=sign*(data_1[index][0])
	elif table==2:
		data=sign*(data_2[index][time])

	return data

########################################
if len(sys.argv)>1:
	ModelName = sys.argv[1]
else:
	ModelName="./testZC"
filename = ModelName+"_res.mat"
	
# apro il .mat file
mat = scipy.io.loadmat(filename)

print "OK:: File correctly loaded"

# strutture contenute nel mat file
data_name = mat['name']
data_1    = mat['data_1']
data_2    = mat['data_2']
dataInfo  = mat['dataInfo']

# numero degli istanti di tempo per la simulazione
time_steps = len(data_2[0][:])-1

print "OK:: data loaded correctly"

names = []
for j in range(len(data_name[0])):
	x = ""

	for i in range(len(data_name[:])):
		try:
			if data_name[i][j]!='\x00':
				x = x + "" + data_name[i][j]
		except:
			print "Index Out of bound - scan Name"
			
	names.append(x)

print "OK:: names scanned succesfully"

Main()
