from hospital_locator import Hospital_Locator
from twilio_handler import Twilio_Handler

class Panic_Handler(object):


    @staticmethod
    def getPanicData(geoLocation,user):
        hospital = Hospital_Locator.findNearestHospital(geoLocation)
        #get user kin data
        curLoc = Hospital_Locator.getCurrentLocation(geoLocation)
        

    def textFamily(family,location):
        message = "Hey "+family['name']+"! I am having a medical  emergency! I am currently at "+location+". Please help me as soon as possible"
        Twilio_Handler.sendMessage(family['number'],message);



