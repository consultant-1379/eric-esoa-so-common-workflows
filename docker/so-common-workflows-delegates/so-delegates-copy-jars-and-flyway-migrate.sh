#!/bin/bash
set -e
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

echo "Migrating with flyway"
flyway-${flyway_version}/./flyway -user=${POSTGRES_USERNAME} -password=${POSTGRES_PASSWORD} -url=${POSTGRES_URL} -configFiles="flyway-${flyway_version}/conf/flyway.config" -locations="classpath:db/migration" migrate
echo "Migration completed"

echo "If SO_DELEGATES_PATH is not provided, the default SO_DELEGATES_PATH is set to /tmp"
if [ -z ${SO_DELEGATES_PATH} ]; then
  SO_DELEGATES_PATH=/tmp
  echo "Setting SO_DELEGATES_PATH to /tmp"
else
  echo "Provided SO_DELEGATES_PATH ${SO_DELEGATES_PATH} to be used"
fi
echo "Deleting so jars from ${SO_DELEGATES_PATH} if it exist"
rm -f ${SO_DELEGATES_PATH}/eric-esoa-workflow-*
rm -f ${SO_DELEGATES_PATH}/eric-esoa-engine-*
rm -f ${SO_DELEGATES_PATH}/domain-orchestrator-consumer-*
rm -f ${SO_DELEGATES_PATH}/domain-orchestrator-*
rm -f ${SO_DELEGATES_PATH}/core-platform-lib-*
rm -f ${SO_DELEGATES_PATH}/engine-client-*
rm -f ${SO_DELEGATES_PATH}/engine-open-api-*
rm -f ${SO_DELEGATES_PATH}/eric-oss-tosca-orchestration-enabler-client-*
rm -f ${SO_DELEGATES_PATH}/eric-oss-tosca-orchestration-enabler-api-*
rm -f ${SO_DELEGATES_PATH}/error-message-factory-*
rm -f ${SO_DELEGATES_PATH}/eric-esoa-common-logging-*

echo "Copying delegate and dependencies jars"
cp -rf /tmp_jars/* ${SO_DELEGATES_PATH}
echo "Copy of jars completed"
ls -lart ${SO_DELEGATES_PATH}
