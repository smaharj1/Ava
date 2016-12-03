import csv

input_path = "/Users/nate/Documents/Products.txt"
drug_output = "./drug_names"
drug_names = {}



with open(input_path) as input_file:

	reader = csv.reader(input_file)

	isfirst = True
	for row in reader:
		if isfirst:
			isfirst = False
			continue		
		
		try:
			pass
		except:
			pass
