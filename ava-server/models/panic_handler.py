from models.twilio_handler import Twilio_Handler
from models.hospital_locator import Hospital_Locator
from db.mongo_client import Mongo_Client
import datetime

class Panic_Handler(object):
	db = Mongo_Client()


	@staticmethod
	def Panic(location, user):
		hospital, current_loc = Panic_Handler.getPanicData(location, user)
		Panic_Handler.textFamily(Panic_Handler.db.GetFamily(user), current_loc, user)



	@staticmethod
	def getPanicData(geoLocation,user):
		hospital = Hospital_Locator.findNearestHospital(geoLocation)
		#get user kin data
		curLoc = Hospital_Locator.getCurrentLocation(geoLocation)
		return (hospital, curLoc,)


	@staticmethod
	def textFamily(family, location,user):
		for member in family:
			message = "Hey "+member['name']+"! This is "+user+". I am having a medical emergency. I am currently at " + \
					location[0]['formatted_address'] + "\n\nThe authorities have been contacted, but please check in.\n\nSent: " + datetime.datetime.now().strftime('%a, %d %b %X' + \
						"\n\nIf you are first to respond, text this number your best diagnosis for instructions (Ex. 'heart attack')")
			Twilio_Handler.sendMessage(member['number'],message);



