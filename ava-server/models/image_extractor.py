import argparse
import base64

from googleapiclient import discovery
from oauth2client.client import GoogleCredentials


class Medicine_Name_Extractor(object):

    @staticmethod
    def getMedicineLabels(imgUrl):
        credentials = GoogleCredentials.get_application_default()
        service = discovery.build('vision', 'v1', credentials=credentials)
        #with open(imgUrl, 'rb') as image:
        #image_content = base64.b64encode(image.read())
        service_request = service.images().annotate(body={
            'requests': [{
                'image': {
                    'content': imgUrl.decode('UTF-8')
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
        labelInfo = response['responses'][0]['textAnnotations'];
        labels = [x['description'] for x in labelInfo];
        print(labels)
        return labels

    
    @staticmethod
    def getMedicineName(imgUrl):
        medicineLabels = Medicine_Name_Extractor.getMedicineLabels(imgUrl);
        