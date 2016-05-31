import httplib
from urlparse import urlparse
import json as JSON
from google.appengine.api import urlfetch

urlfetch.set_default_fetch_deadline(30)

class Error(Exception):
	"""Exceptions"""

class HttpError(Error):
	"""Exception"""

class Response(object):

    def __init__(self, request):
        self.request = request

    def parse(self, response):
        self.status = response.status
        self.reason = response.reason
        self.headers = dict(response.getheaders())
        self.body = response.read()
        return self

    def validate(self, codes):
        if self.status not in codes:
            raise HttpError('Status expected: %s, but was: %d %s: %.100s...' % (codes, self.status, self.reason, self.body))
        return self

    def body_json(self):
		return JSON.loads(self.body)

class Request(object):

    def __init__(self, method, url):
        self._method = method or 'GET'
        url_parsed = urlparse(url)
        self._host = url_parsed.hostname
        self._uri = url_parsed.path
        self._query = url_parsed.query
        self._scheme = url_parsed.scheme
        self._port = url_parsed.port
        self._body = None
        self._headers = {}

    def body_json(self, obj):
        self._headers['Content-Type'] = 'application/json'
        self._body = JSON.dumps(obj)
        return self

    def _create_connection(self):
    	if self._scheme == 'https':
            return httplib.HTTPSConnection(self._host, self._port or 443)
        return httplib.HTTPConnection(self._host, self._port or 80)

    def execute(self):
        conn = self._create_connection()
        try:
            uri = self._uri
            if self._query:
                uri = '%s?%s' % (self._uri, self._query)
            conn.request(self._method, uri, self._body, self._headers)
            resp = conn.getresponse()
            return Response(self).parse(resp)
        finally:
            if conn:
                conn.close()

if __name__ == '__main__':
    resp = Request('GET', 'http://google.com').execute().validate([ 200, 301 ])
    print resp.status, resp.reason
    print resp.headers
    print resp.body
