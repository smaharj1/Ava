from googleplaces import GooglePlaces, types, lang
API_KEY = "AIzaSyAGGXKLGRwsE2yNDtSVoX6jm4CMTsVzMYU"
google_places = GooglePlaces(API_KEY)

class Hospital_Locator:    
    
    @staticmethod
    def findNearestHospital(location):
        geoLocation = {"lat":location[0],"lng":location[1]}
        query_result = google_places.nearby_search(
        lat_lng=geoLocation, types=[types.TYPE_HOSPITAL]);
        hospital = query_result.places[0];
        hospital.get_details()
        return {
            "name":hospital.name,
            "location":hospital.geo_location,
            "phone":hospital.local_phone_number,
            "website":hospital.website
        }
