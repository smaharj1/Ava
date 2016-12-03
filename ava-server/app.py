from flask import Flask, render_template, url_for, request, session, redirect, Response
from flask_pymongo import PyMongo


app = Flask(__name__)
db = Mongo(app)

@app.route('/', methods=['GET'])
def Index():
	return "Welcome to Ava"

@app.route('/nearestHospital', methods=['POST'])
def GetNearestHospital():
	return "Blerp"




