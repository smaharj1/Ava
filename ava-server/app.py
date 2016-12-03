from flask import Flask, render_template, url_for, request, session, redirect, Response
from db.mongo_client import Mongo_Client
import json, datetime, time
from models.panic_handler import Panic_Handler as ph
from models.hospital_locator import Hospital_Locator as hl
import twilio.twiml
from models.procedure_lookup import *

DOW = [
	"Monday", 
	"Tuesday",
	"Wednesday",
	"Thursday",
	"Friday",
	"Saturday",
	"Sunday"
]


app = Flask(__name__)
db = Mongo_Client()

from models.image_extractor import Medicine_Name_Extractor as mne

@app.route('/textme', methods=['GET', 'POST'])
def textMe():
	recvText = request.values.get('Body',None)
	msg = look_up_procedure(recvText)
	resp = twilio.twiml.Response()
	resp.message(msg)
	return str(resp)



@app.route('/', methods=['GET'])
def Index():
	return "Welcome to Ava"

@app.route('/nearestHospital', methods=['POST'])
def GetNearestHospital():
	#send loc as a dictionary of lat and lng
	data = json.dumps(hl.findNearestHospital(loc))
	return data

@app.route('/getUserData', methods=['GET', 'POST'])
def GetUserData():
	return json.dumps(db.GetUserData())


@app.route('/createNewUser', methods=['GET', 'POST'])
def CreateNewUser():
	db.CreateNewUser({"firstName":"Bishal",
						"lastName":"Regmi",
						"prescriptions" : [["Adderall", []]],
						"scheduled_medications":{
							"Monday" : [
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Tuesday": [
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Wednesday":[
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Thursday":[
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Friday":[
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Saturday":[
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [12, 0],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}],
							"Sunday":[
							{'medication' : 'Adderall',
								 'time' : [9, 30],
								 'times_missed' : 0 },
								 {'medication' : 'Adderall',
								 'time' : [17, 0],
								 'times_missed' : 0}]
						},
						"medication" : [],
						"ice" : [
							{
								'name' : "Nate",
								'number' : '+19084774708'
							},
							{
								'name' : "Sujil",
								'number' : '+12016754068'
							}
						]})
	return json.dumps({})

@app.route('/panic', methods=['GET', 'POST'])
def PANIC_AT_THE_DISCO():
	lat, lng = request.form['lat'], request.form['lng']
	ph.Panic({'lat' : lat, 'lng' : lng}, "Bishal")
	return json.dumps({})

@app.route('/medicine', methods=['GET', 'POST'])
def GetMedicine():
	data = request.form.getlist('image')
	labels = mne.getMedicineName(data)
	if not labels:
		return json.dumps({})

	print(labels)
	db.AddPrescription(labels)

	return json.dumps(labels)

@app.route('/prescriptions', methods=['GET'])
def GetPrescriptions():
	data = db.GetPrescriptions()

	if not data:
		return json.dumps({})

	return json.dumps(data)

@app.route('/getReminders', methods=['GET'])
def GetReminders():
	day = request.form.getlist('day')
	data = db.GetRemindersByDay(day)

	if not data:
		return json.dumps({})

	return json.dumps(data)

@app.route('/nextReminder', methods=['GET'])
def GetNextReminder():

	day = DOW[datetime.date.today().weekday()]
	time = datetime.datetime.now()
	t = time.strftime("%H %M")
	t = [int(x) for x in t.split()]
	data = db.GetRemindersByDay(day)
	if data == []:
		return json.dumps({})
	for entry in data:
		if entry['time'][0] == t[0]:
			if entry['time'][1] >= t[1]:
				return json.dumps(entry)
		elif entry['time'][0] > t[0]:
			return json.dumps(entry)

	return json.dumps({})


