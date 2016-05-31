import webutil
import oauth
import re
import logging
from google.appengine.api import mail
from google.appengine.api import app_identity

def is_valid(val):
    if not val:
        return False
    valid_regex = re.compile('^[a-zA-Z0-9\-]{1,20}$')
    return valid_regex.match(val)

def load_users():
    req = oauth.Request('GET', 'https://sheets.googleapis.com/v4/spreadsheets/1LoQmZ7Qe6PfRvmMXfziW8mmZOJDLECm3lysD52MD870/values/auth')
    resp = req.execute().validate([200]).body_json()
    users = {}
    for value in resp.get('values') or []:
        user = value[0]
        password = value[1]
        if is_valid(user) and is_valid(password):
            users[user] = password
        else:
            logging.warn('Invalid user or password "%s":"%s"' % (user, password))
    return users


class MailService(webutil.BaseHandler):

    def auth(self):
        username, password = self.req_user()
        logging.info('Auth %s' % (username))
        if not is_valid(username) or  not is_valid(password):
            raise webutil.UnauthorizedError()
        users = load_users()
        stored_pass = users.get(username)
        if not stored_pass:
            raise webutil.UnauthorizedError()
        if password != stored_pass:
            raise webutil.UnauthorizedError()
        return username

    def parse_mail_message(self):
        ret = self.req_json()
        attachments = ret.get('attachments') or []
        print 'aaa'
        ret['attachments'] = [ ( attach['name'], attach['body'] ) for attach in attachments ]

    def post(self):
        message = self.parse_mail_message()
        username = self.auth()

        sender = '%s@%s.appspotmail.com' % (username, app_identity.get_application_id())
        mail.send_mail(sender=sender,
            to=message['to'],
            cc=message.get('cc'),
            bcc=message.get('bcc'),
            reply_to=message.get('reply_to'),
            subject=message['subject'],
            body=message.get('text'),
            html=message.get('html'),
            attachments=message['attachments'])

        self.resp_json('OK')
