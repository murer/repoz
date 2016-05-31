import webutil
import oauth
import re
import logging

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
        logging.info('Auth %s:%s' % (username, password))
        if not is_valid(username) or  not is_valid(password):
            raise webutil.UnauthorizedError()
        users = load_users()
        stored_pass = users.get(username)
        if not stored_pass:
            raise webutil.UnauthorizedError()
        print 'x', username, stored_pass, password
        if password != stored_pass:
            raise webutil.UnauthorizedError()

    def get(self):
        self.auth()
        self.resp_json('aaaa')
