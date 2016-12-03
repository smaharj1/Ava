from googleplaces import GooglePlaces, types, lang
import googlemaps

API_KEY = "AIzaSyAGGXKLGRwsE2yNDtSVoX6jm4CMTsVzMYU"


class Hospital_Locator(object):    
    
    google_places = GooglePlaces(API_KEY)
    gmaps = googlemaps.Client(API_KEY)
    
    @staticmethod
    def findNearestHospital(location):
        geoLocation = location
        query_result = Hospital_Locator.google_places.nearby_search(
        lat_lng=geoLocation, types=[types.TYPE_HOSPITAL]);
        hospital = query_result.places[0];
        hospital.get_details()
        return {
            "name":hospital.name,
            "location":Hospital_Locator.getCurrentLocation(hospital.geo_location)[0]['formatted_address'],
            "phone":hospital.local_phone_number,
            "website":hospital.website
        }

    @staticmethod
    def getCurrentLocation(location):
        myLoc = Hospital_Locator.gmaps.reverse_geocode(location)
        return myLoc

