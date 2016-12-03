from twilio.rest import TwilioRestClient

account_sid = ""
auth_token = ""
client = TwilioRestClient(account_sid,auth_token)
myNumber = "+12015741880"


class Twilio_Handler(object):

    def sendMessage(number, phone):
        client.messages.create(to=number, from_=myNumber, body=message)

    