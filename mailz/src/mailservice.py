import webutil
import oauth
import re
import logging
import base64
import hashlib
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
        if is_valid(user) and password:
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
        m = hashlib.sha256()
        m.update(password)
        hashed_password = m.hexdigest()
        print 'xxxx', hashed_password, stored_pass
        if hashed_password != stored_pass:
            raise webutil.UnauthorizedError()
        return username

    def parse_mail_message(self):
        ret = self.req_json()
        attachments = ret.get('attachments') or []
        ret['attachments'] = [ ( attach['name'], base64.b64decode(attach['body']) ) for attach in attachments ]
        return ret

    def post(self):
        message = self.parse_mail_message()
        username = self.auth()

        sender = '%s@%s.appspotmail.com' % (username, app_identity.get_application_id())
        msg = mail.EmailMessage(sender=sender,
            to=message['to'],
            subject=message['subject'])

        if message.get('cc'):
            msg.cc = message.get('cc')
        if message.get('bcc'):
            msg.bcc = message.get('bcc')
        if message.get('reply_to'):
            msg.reply_to = message.get('reply_to')
        if message.get('body'):
            msg.body = message.get('body')
        if message.get('html'):
            msg.html = message.get('html')
        msg.attachments = message['attachments']
        msg.send()

        self.resp_json('OK')
