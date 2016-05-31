from google.appengine.api import app_identity
import http

SCOPES = [
    'https://www.googleapis.com/auth/drive',
    'https://www.googleapis.com/auth/spreadsheets'
]

class Request(http.Request):

    def execute(self):
        print 'xxx', SCOPES
        auth_token, _ = app_identity.get_access_token(SCOPES)
        self._headers['Authorization'] = 'Bearer %s' % (auth_token)
        return super(Request, self).execute()
