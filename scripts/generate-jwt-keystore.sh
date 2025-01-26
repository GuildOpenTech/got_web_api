#!/bin/bash

# Configuration
KEYSTORE_FILE="src/main/resources/jwt-keystore.p12"
KEYSTORE_PASSWORD="changeit"
KEY_ALIAS="got-jwt-key"
VALIDITY=3650 # 10 ans

# Création du keystore avec une paire de clés RSA
keytool -genkeypair \
  -alias $KEY_ALIAS \
  -keyalg RSA \
  -keysize 4096 \
  -validity $VALIDITY \
  -storetype PKCS12 \
  -keystore $KEYSTORE_FILE \
  -storepass $KEYSTORE_PASSWORD \
  -dname "CN=Got Web, OU=Security, O=Got Web, L=Paris, ST=IDF, C=FR"

# Vérification du keystore
echo "\nVérification du keystore :"
keytool -list -v -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD
