
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
		cursor = self.db[self.collection].find()
		data = cursor[0]
		del data['_id']
		return data


	def CreateNewUser(self, data):
		self.db[self.collection].insert_one(data)


	def GetFamily(self, user):
		cursor = self.db[self.collection].find()
		return cursor[0]['ice']


	def GetRemindersByDay(self, day):
		data = self.db[self.collection].find()
		return data[0]['scheduled_medications'][day]


	def AddPrescription(self, medication, doses=[]):
		current = self.db[self.collection].find({"firstName":"Bishal"})[0]
		current['prescriptions'].append({'medication':medication, 'doses':doses})
		self.db[self.collection].update({"firstName":"Bishal"}, {"$set": current}, upsert=False)


	def GetPrescriptions(self):
		return self.db[self.collection].find({"firstName":"Bishal"})[0]['prescriptions']



