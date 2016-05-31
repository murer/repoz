# Mailz

## Create user

<a href="https://docs.google.com/spreadsheets/d/1LoQmZ7Qe6PfRvmMXfziW8mmZOJDLECm3lysD52MD870/edit#gid=0">
  https://docs.google.com/spreadsheets/d/1LoQmZ7Qe6PfRvmMXfziW8mmZOJDLECm3lysD52MD870/edit#gid=0
</a>

Encrypt your password with sha256
```shell
echo -n 'anypassword' | openssl sha256
```

You will use http basic authentication with this credential.

The email sender will be ```<username>@cloudcontainerz.appspotmail.com```

## Sending a email

```shell
curl 'https://<username>:<password>@mailz-dot-cloudcontainerz.appspot.com/s/mail' \
    -H 'Expect: ' -H 'Content-Type: application/json' \
    --data-binary '@message.json'
```

message.json

```json
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
```

As you can see, attachments must be base64 encoded.
