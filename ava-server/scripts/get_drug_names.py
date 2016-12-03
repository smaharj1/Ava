import csv
import json

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
			if not row[3] in drugs_names[row[5]]:
				drug_names[row[5]] += row[3].split(";")
			else:
				continue
		except:
			drug_names[row[5]] = row[3].split(";")


with open(drug_output, 'w') as outfile:
	json.dump(drug_names, outfile)


