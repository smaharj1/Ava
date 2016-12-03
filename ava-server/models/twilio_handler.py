from twilio.rest import TwilioRestClient

account_sid = "AC6cde470a46432c2d0860e433c15c3e7c"
auth_token = "9e5655649b6730541da36baf87fd6051"
client = TwilioRestClient(account_sid,auth_token)
myNumber = "+12015741880"


class Twilio_Handler(object):

    def sendMessage(number, message):
        client.messages.create(to=number, from_=myNumber, body=message)

    