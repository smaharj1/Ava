from models.twilio_handler import Twilio_Handler
from models.hospital_locator import Hospital_Locator
from db.mongo_client import Mongo_Client

class Panic_Handler(object):
	db = Mongo_Client()


	@staticmethod
	def Panic(location, user):
		hospital, current_loc = Panic_Handler.getPanicData(location, user)
		print(current_loc)
		Panic_Handler.textFamily(Panic_Handler.db.GetFamily(user), current_loc)



	@staticmethod
	def getPanicData(geoLocation,user):
		hospital = Hospital_Locator.findNearestHospital(geoLocation)
		#get user kin data
		curLoc = Hospital_Locator.getCurrentLocation(geoLocation)
		return (hospital, curLoc,)


	@staticmethod
	def textFamily(family, location):
		for member in family:
			message = "Hey "+member['name']+"! I am having a medical emergency. I am currently at " \
					+location[0]['formatted_address'] +". Please help me as soon as possible"
			Twilio_Handler.sendMessage(member['number'],message);



