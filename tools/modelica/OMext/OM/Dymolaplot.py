#####################################################################
# graphical representation variables with python                    #
#####################################################################
import scipy.io
import pylab
import numpy as np


# load the .mat file
mat = scipy.io.loadmat('./Test_res.mat')

print "OK:: File correctly loaded"

# structures contained into the .mat file
data_name = mat['name']
data_1    = mat['data_1']
data_2    = mat['data_2']
dataInfo  = mat['dataInfo']

# getting all the time steps provided by dymola
time = data_2[0][:]
print "OK:: simulation time correctly loaded correctly"

# scanning data provided by .mat file and creating the array of names

# list that contains the name of the variables, initialization with an empty value
names = [""]

print "OK:: starting scanning names:"

for j in range(len(data_name[0])):
	x = ""

	for i in range(len(data_name[:])):
		if data_name[i][j]!=' ':
			x = x + "" + data_name[i][j]
	
	names.append(x)

print "OK:: names array created"

######################################################
# first figure
# the first element has no meaning, it can be removed
names.remove("")

temp = ""
while temp!='QUIT':
	temp = raw_input("If you want to create a new plot press enter, otherwise digit QUIT: ")
	if temp == 'QUIT' :
		# quit
		print "EXIT"
	else:
		variableNames = [""]
		read = 'true'
		while read == 'true':
			String = str(raw_input("Insert the variable name to plot, otherwise digit EXIT: "))
			if String == 'EXIT' :
				read = 'false'
			else:
				# append the variable name
				variableNames.append(String)
	
		# remove the first label, used in the initialization		
		variableNames.remove("")	
	
		# find the position of each variable
		if len(variableNames)>=1:
			# position of the first variable
			positions = [names.index(variableNames[0])]
			# position of other variables
			for j in range(len(variableNames)-1):
				positions.append(names.index(variableNames[j+1]))

		
			data = np.zeros([len(positions),len(time)])
		
			for i in range(len(positions[:])):
				table = dataInfo[0][positions[i]]
				index = abs(dataInfo[1][positions[i]])-1 # this -1 is because the matlab notation starts with i = 1,...
				
				if dataInfo[1][positions[i]]>= 0:
					sign = 1
				else:
					sign = -1
					
				for t in range(len(time)):
					if table==1 :
						data[i][t]=sign*(data_1[index][0])
					elif table==2:
						data[i][t]=sign*(data_2[index][t])
	
		# at this point it is possible to create a plot containing the data collected
		fig = pylab.figure()
		a = fig.add_subplot(111)
		
		timing = str(raw_input("What time scale do you prefer (s,m,h,d)? "))
		
		if timing=='h':
			tdiv = 3600
			x_label = 'time (h)'
		elif timing=='m':
			tdiv = 60
			x_label = 'time (min)'
		elif timing=='d':
			tdiv = 86400
			x_label = 'time (day)'
		else:
			tdiv = 1
			x_label = 'time (s)'
			
		for i in range(len(positions[:])):
			a.plot(time/tdiv, data[i][:])
		
		leg = a.legend(variableNames,'upper right', shadow=True)
		
		a.grid(True)
		a.set_xlabel(x_label)
		a.set_ylabel(raw_input('y axis label: '))
		a.set_title(raw_input('plot title: '))

		
		fig.show()
		
		# delete all the information
		while len(variableNames)>0:
			variableNames.pop()
		while len(positions)>0:
			positions.pop()
		data = 0
		
