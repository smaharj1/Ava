import json
import editdistance

def look_up_procedure(message):
	message = message.strip().lower()
	data = {}
	with open("./data/procedures.json") as file:
		data = json.load(file)

	response = ""

	try:
		response = data[message]
		return response
	except:
		pass

	keys = data.keys()

	best = ("", 10000000000)

	for key in keys:
		distance = editdistance.eval(key, message)
		if distance < best[1]:
			best = (key, distance)

	return data[best[0]]

