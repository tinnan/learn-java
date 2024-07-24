#!/bin/bash

WORK_DIR=./build/certgen
rm -rf "$WORK_DIR"
mkdir -p "$WORK_DIR"
cd "$WORK_DIR" || exit

# From this guide https://github.com/Azure/Azurite/blob/main/README.md#openssl
# Generate Cert and Key
# The -subj values are required, but do not have to be valid.
# !! The subjectAltName must contain the Azurite IP address.
openssl req -newkey rsa:2048 -x509 -nodes -keyout key.pem -new -out cert.pem -sha256 \
  -days 36500 -addext "subjectAltName=IP:127.0.0.1" -subj "/C=TH/ST=ST/L=LO/O=OR/OU=OU/CN=CN"

# Convert the Certificate to PKCS12 Format
ALIAS="azurite"
SRC_PWD="azurite@1234"
openssl pkcs12 -export -in cert.pem -inkey key.pem -out cert.p12 -name "$ALIAS" \
  -password "pass:$SRC_PWD"
# Import PKCS12 Keystore into Java Keystore
KEYSTORE_PWD="azurite@1234"
keytool -importkeystore -srckeystore cert.p12 -srcstoretype PKCS12 -srcstorepass "$SRC_PWD" \
  -destkeystore keystore.jks -deststoretype pkcs12 -deststorepass "$KEYSTORE_PWD"
# Import Certificate into Java Truststore
TRUSTSTORE_PWD="azurite@1234"
keytool -import -file cert.pem -alias "$ALIAS" -storepass "$TRUSTSTORE_PWD" -noprompt \
  -keystore truststore.jks
