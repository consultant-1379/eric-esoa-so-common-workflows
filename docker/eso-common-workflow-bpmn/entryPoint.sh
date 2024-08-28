#!/bin/bash
#
# COPYRIGHT Ericsson 2023
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

echo "Configuring tls certificate in java"
tls_enabled=${tls_enabled:-true}
if [ "$tls_enabled" = true ]; then
  echo "TLS is enabled"
  if [ -z ${TLS_CERT_PATH} ]; then
    TLS_CERT_PATH=/tmp
    echo "Setting TLS_CERT_PATH to /tmp"
  else
    echo "Provided TLS_CERT_PATH ${TLS_CERT_PATH} to be used"
  fi

  mkdir ${TLS_CERT_PATH}/individualCerts && cd $_
  FILE_COUNT=$(csplit -f individual- ${TLS_MNT_CERT_PATH}/iam/ca.crt '/-----BEGIN CERTIFICATE-----/' '{*}' --elide-empty-files | wc -l)
  echo "Number of certs in cacert bundle is ${FILE_COUNT}"
  for i in $(ls); do
    echo "Adding ${i} to java keystore ${DEFAULT_JAVA_CACERTS}"
    keytool -storepass 'changeit' -noprompt -trustcacerts -importcert -file ${i} -alias ${i} -keystore $DEFAULT_JAVA_CACERTS 2>&1
  done
else
  echo "TLS is disabled, skipping Certificate addition to java cert store"
fi

java ${JAVA_OPTS} -jar /eric-so-common-workflows-bpmn.jar
