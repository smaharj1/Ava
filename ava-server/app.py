from flask import Flask, render_template, url_for, request, session, redirect, Response
from db.mongo_client import Mongo_Client
import json, datetime
from models.panic_handler import Panic_Handler as ph

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


@app.route('/', methods=['GET'])
def Index():
	return "Welcome to Ava"

@app.route('/nearestHospital', methods=['POST'])
def GetNearestHospital():
	return "Blerp"

@app.route('/getUserData', methods=['GET', 'POST'])
def GetUserData():
	data = json.dumps(db.GetUserData())
	return data

@app.route('/createNewUser', methods=['GET', 'POST'])
def CreateNewUser():
	db.CreateNewUser({"firstName":"Bishal",
						"lastName":"Regmi",
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

@app.route('/panic', methods=['GET'])
def PANIC():
	loc = {'lat' : 40.6089687, 'lng' : -75.3781199}
	ph.Panic(loc, "Bishal")
	return json.dumps({})

@app.route('/medicine', methods=['GET', 'POST'])
def GetMedicine():
	data = request.form.getlist('image')
	labels = mne.getMedicineName(data)
	print(labels)
	return json.dumps({})

@app.route('/prescriptions', methods=['GET'])
def GetPrescriptions():
	return json.dumps({})

@app.route('/getReminders', methods=['GET'])
def GetReminders():
	return json.dumps({})

@app.route('/nextReminder', methods=['GET'])
def GetNextReminder():

	# Get todays date and time

	# Get the next scheduled medication in the db

	# return it

	day = DOW[datetime.date.weekday()]
	print(day)
	return json.dumps({})


