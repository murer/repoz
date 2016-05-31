import json
import webapp2
import base64
import logging

class Error(Exception):
    """ Error """

class HttpError(Error):
    def __init__(self, code):
        self.code = code
        self.headers = {}

class UnauthorizedError(HttpError):
    def __init__(self):
        super(UnauthorizedError, self).__init__(401)
        self.headers['WWW-Authenticate'] = 'Basic realm="mailz"'

class NotFoundError(HttpError):
    def __init__(self):
        super(NotFoundError, self).__init__(404)

class ForbiddenError(HttpError):
    def __init__(self):
        super(NotFoundError, self).__init__(403)

def trim(string):
    if string == None:
        return None
    string = str(string).strip()
    if len(string) == 0:
        return None
    return string

class BaseHandler(webapp2.RequestHandler):

    def secure(self):
        return False

    def dispatch(self):
        try:
            if self.secure():
                self.check_user()
            super(BaseHandler, self).dispatch()
        except HttpError, e:
            self.response.set_status(e.code)
            self.response.headers.update(e.headers)
            self.resp_text('Error: %i\n' % e.code)

    def req_json(self):
        string = trim(self.request.body)
        if string == None:
            return None
        return json.loads(string)


    def resp_text(self, obj):
        self.response.headers['Content-Type'] = 'text/plain; charset=UTF-8'
        self.response.out.write(obj)

    def resp_json(self, obj):
        self.response.headers['Content-Type'] = 'application/json; charset=UTF-8'
        self.response.out.write(json.dumps(obj, indent=True) + '\n')

    def req_header(self, name):
        return trim(self.request.headers.get(name))

    def get_user(self):
        header = self.req_header('Authorization')
        if header == None:
            return None, None
        header = header.replace('Basic ', '')
        header = base64.b64decode(header)
        header = header.split(':')
        username = trim(header[0])
        password = trim(header[1])
        if not username:
            return None, None
        return username, password or ''

    def req_user(self):
        user, password = self.get_user()
        if not user:
            raise UnauthorizedError()
        return user, password

    def is_cron(self):
        header = self.req_header('X-AppEngine-Cron')
        if header == 'true':
            return True
        return False

    def check_user(self):
        if self.is_cron():
            logging.info('auth cron')
            return
        username, passsword = self.req_user()
        if username != 'admin' and password != 'wr4th0fg0ds':
            raise UnauthorizedError()
        logging.info('auth basic %s', username)

    def param(self, name):
        return trim(self.request.get(name))
