
import csv

print("Processing survival data ...")

data_path = "/Users/nate/Downloads/Timely_and_Effective_Care_-_Hospital.csv"
output_path = "/tmp/data/effective_response_data.csv"

def GetAddressFromRow(row):
	return ""


with open(data_path) as input_file:
	with open(output_path, 'w') as output_file:
		reader = csv.reader(input_file)
		writer = csv.writer(output_file)
		
		isfirst = True
		for row in reader:
			if isfirst:
				isfirst = False
				continue
			try:
				if row[11] == "Not Available":
					continue
				dirty_string = row[16]

				begin = str.find(dirty_string, '(')
				end = str.find(dirty_string, ')')
				if begin == -1 or end == -1:
					continue
				coords = dirty_string[begin+1:end]
				coords = coords.split(", ")
				coords = [float(x) for x in coords]
				condition = row[8]
				
				writer.writerow([condition]+coords+[row[11]])

			except Exception as e:
				print(str(e))
				continue





