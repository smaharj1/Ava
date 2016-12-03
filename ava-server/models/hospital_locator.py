from googleplaces import GooglePlaces, types, lang

API_KEY = "AIzaSyAGGXKLGRwsE2yNDtSVoX6jm4CMTsVzMYU"


class Hospital_Locator:    
    
    google_places = GooglePlaces(API_KEY)
    
    @staticmethod
    def findNearestHospital(location):
        geoLocation = {"lat":location[0],"lng":location[1]}
        query_result = Hospital_Locator.google_places.nearby_search(
        lat_lng=geoLocation, types=[types.TYPE_HOSPITAL]);
        hospital = query_result.places[0];
        hospital.get_details()
        return {
            "name":hospital.name,
            "location":hospital.geo_location,
            "phone":hospital.local_phone_number,
            "website":hospital.website
        }

    @staticmethod
    def getCurrentLocation(location):
        myLoc = google_places.latlng_to_address(location[0],location[1])
        return myLoc

