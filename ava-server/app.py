from flask import Flask, render_template, url_for, request, session, redirect, Response
from flask_pymongo import PyMongo
from db.mongo_client import Mongo_Client
import json
from models.image_extractor import Medicine_Name_Extractor as mne


app = Flask(__name__)
db = Mongo_Client()



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
	db.CreateNewUser({"firstName":"Nate",
						"lastName":"Moon",
						"scheduled_medications":{
							"Monday" : [],
							"Tuesday": [],
							"Wednesday":[],
							"Thursday":[],
							"Friday":[],
							"Saturday":[],
							"Sunday":[]
						}})
	return json.dumps({})


@app.route('/panic', methods=['GET'])
def PANIC():
	return json.dumps({})

@app.route('/medicine', methods=['GET', 'POST'])
def GetMedicine():
	imgURL = request.form['url']
	labels = gmne.getMedicineLabels(imgURL)
	return json.dumps({})

@app.route('/prescriptions', methods=['GET'])
def GetPrescriptions():
	return json.dumps({})

@app.route('/getReminders', methods=['GET'])
def GetReminders():
	return json.dumps({})

@app.route('/nextReminder', methods=['GET'])
def GetNextReminder():
	return json.dumps({})


