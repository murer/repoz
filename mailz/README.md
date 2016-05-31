# Mailz

<p>Add a user here:</p>
<a href="https://docs.google.com/spreadsheets/d/1LoQmZ7Qe6PfRvmMXfziW8mmZOJDLECm3lysD52MD870/edit#gid=0">
  https://docs.google.com/spreadsheets/d/1LoQmZ7Qe6PfRvmMXfziW8mmZOJDLECm3lysD52MD870/edit#gid=0
</a>
<p>echo -n 'anypassword' | openssl sha256</p>
<pre><code>
  {
    "to": ["a4@sample.com"],
    "body": "aaa",
    "subject": "s1",
    "reply_to": "a3@sample.com",
    "cc": ["a2@sample.com"],
    "bcc": ["a1@sample.com"],
    "body": "body text",
    "html": "<html><body><h1>html text</h1><p>:)</p></body></html>",
    "attachments": [{
      "name": "abc.txt",
      "body": "dGV4dAo="
    }, {
      "name": "other.txt",
      "body": "Y29udGVudCBtdXN0IGJlIGJhc2U2NCBlbmNvZGVkCg=="
    }]
  }
</code></pre>
