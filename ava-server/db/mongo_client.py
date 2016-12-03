
import pymongo as pm

class Mongo_Client(object):

	def __init__(self, db_address='mongodb://localhost:27017/', 
					db_name="Ava",
					collection="UserData"):
		self.client = pm.MongoClient(db_address)
		self.db = self.client[db_name]
		self.collection = collection
		print(" * Mongo client initialized...")

	def GetUserData(self):
		cursor = self.db[self.collection].find({"name":"Nate"})
		data = cursor[0]
		del data['_id']
		return data


	def CreateNewUser(self, data):
		self.db[self.collection].insert_one(data)


	def GetFamily(self, user):
		cursor = self.db[self.collection].find()
		return cursor[0]['ice']

