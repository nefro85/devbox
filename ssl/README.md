# SSL


### OpenSSL

```bash
PSWD="my-password"

openssl genrsa -aes256 -passout pass:${PSWD} -out server.pass.key 4096

openssl rsa -passin pass:${PSWD} -in server.pass.key -out server.key
# save the RSA key
rm server.pass.key

# When the openssl req command asks for a “challenge password”, just press return, leaving the password empty.
openssl req -new -key server.key -out server.csr

# SSL Cert
openssl x509 -req -sha256 -days 365 -in server.csr -signkey server.key -out server.crt
# (1) cert:  server.crt
# (2) key:   server.key
```

refs:
 - https://gist.github.com/mtigas/952344

### JKS

(JKS manager)[https://github.com/sfkulyk/jks-manager/]

```bash
curl -OL https://raw.githubusercontent.com/sfkulyk/jks-manager/master/jks_mgr.sh
```
