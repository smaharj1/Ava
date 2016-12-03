from flask import Flask, render_template, url_for, request, session, redirect, Response
from flask_pymongo import PyMongo
from db.mongo_client import Mongo_Client
import json

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
						"scheduled_medications":[[9, 30], [17, 0]]})
	return json.dumps({})


@app.route('/panic', methods=['GET'])
def PANIC():
	return json.dumps({})

@app.route('/medicine', methods=['GET', 'POST'])
def GetMedicine():
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


