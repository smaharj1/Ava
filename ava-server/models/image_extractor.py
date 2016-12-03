import argparse
import base64
import json
from googleapiclient import discovery
from oauth2client.client import GoogleCredentials


class Medicine_Name_Extractor(object):

    @staticmethod
    def getMedicineLabels(imgUrl):
        imgUrl = imgUrl[0]
        credentials = GoogleCredentials.get_application_default()
        service = discovery.build('vision', 'v1', credentials=credentials)
        #with open(imgUrl, 'rb') as image:
        #image_content = base64.b64encode(image.read())

        service_request = service.images().annotate(body={
            'requests': [{
                'image': {
                    'content': imgUrl
                },
                'features': [
                {
                    'type':'TEXT_DETECTION',
                    'maxResults': 10
                },
                ]
            }]
        })
        response = service_request.execute()
        
        try:
            labelInfo = response['responses'][0]['textAnnotations'];
        except:
            return []

        labels = [x['description'] for x in labelInfo];
        
        return labels

    
    @staticmethod
    def getMedicineName(imgUrl):
        medicineLabels = Medicine_Name_Extractor.getMedicineLabels(imgUrl);
        medicineList = {}
        with open("./data/drug_names") as medicineFile:
            medicineList = json.load(medicineFile)

        for label in medicineLabels:
            if label.upper() in medicineList:
                return label

        