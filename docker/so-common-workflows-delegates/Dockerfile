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

FROM armdocker.rnd.ericsson.se/proj-esoa-so/so-base-openjdk17:1.3.2-1

ENV ARTIFACTORY https://arm.seli.gic.ericsson.se/artifactory/proj-service-orchestrator-dev
ENV PROJ_SO_RELEASE_BOS https://arm.seli.gic.ericsson.se/artifactory/proj-service-orchestrator-release-local/com/ericsson/bos/so
ENV PROJ_SO_RELEASE_LOCAL_CORE https://arm.epk.ericsson.se/artifactory/proj-service-orchestrator-release-local/com/ericsson/oss/orchestration/so/service/core
ENV PROJ_SO_RELEASE_LOCAL_ENABLER https://arm.epk.ericsson.se/artifactory/proj-service-orchestrator-release-local/com/ericsson/oss/orchestration/so/workflow/enabler
ENV NEXUS_COM_SO_COMMON https://arm1s11-eiffel052.eiffel.gic.ericsson.se:8443/nexus/content/groups/eo-repositories/com/ericsson/oss/orchestration/so/common
ENV PROJ_SO_RELEASE_LOCAL_SSLO https://arm.epk.ericsson.se/artifactory/proj-service-orchestrator-release-local/com/ericsson/oss/sslo
ENV flyway_version 10.11.0

ARG uid=191974
ARG gid=191974

# Copy script
COPY docker/so-common-workflows-delegates/so-delegates-copy-jars-and-flyway-migrate.sh /so-delegates-copy-jars-and-flyway-migrate.sh

# Install curl and flyway tool
RUN mkdir tmp_jars  \
    && curl --fail -O "${ARTIFACTORY}/org/flywaydb/flyway-commandline/${flyway_version}/flyway-commandline-${flyway_version}-linux-x64.tar.gz" \
    && tar -xvf flyway-commandline-${flyway_version}-linux-x64.tar.gz \
    && rm flyway-commandline-${flyway_version}-linux-x64.tar.gz  \
    && mkdir flyway-${flyway_version}/sql \
    && mkdir flyway-${flyway_version}/jars \
    && chown -R $uid:$gid flyway-${flyway_version}/ \
    && chmod +x so-delegates-copy-jars-and-flyway-migrate.sh

# Delegates and their dependencies' versions
ARG error_message_factory
ARG eric_eo_workflow
ARG eric_eo_engine
ARG domain_orchestrator_consumer
ARG domain_orchestrator
ARG eric_esoa_common_logging
ARG toe_api
ARG toe_client

# Pull relevant jars
RUN cd /tmp_jars  \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_CORE}/eric-esoa-workflow-delegates/${eric_eo_workflow}/eric-esoa-workflow-delegates-${eric_eo_workflow}.jar"  \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_CORE}/eric-esoa-engine-delegates/${eric_eo_engine}/eric-esoa-engine-delegates-${eric_eo_engine}.jar"  \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_ENABLER}/domain-orchestrator-consumer-delegates/${domain_orchestrator_consumer}/domain-orchestrator-consumer-delegates-${domain_orchestrator_consumer}.jar" \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_ENABLER}/domain-orchestrator-delegates/${domain_orchestrator}/domain-orchestrator-delegates-${domain_orchestrator}.jar"  \
    && curl --fail -O "${NEXUS_COM_SO_COMMON}/error-message-factory/${error_message_factory}/error-message-factory-${error_message_factory}.jar" \
    && curl --fail -O "${PROJ_SO_RELEASE_BOS}/eric-esoa-common-logging/${eric_esoa_common_logging}/eric-esoa-common-logging-${eric_esoa_common_logging}.jar" \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_SSLO}/eric-oss-tosca-orchestration-enabler-api/${toe_api}/eric-oss-tosca-orchestration-enabler-api-${toe_api}.jar" \
    && curl --fail -O "${PROJ_SO_RELEASE_LOCAL_SSLO}/eric-oss-tosca-orchestration-enabler-client/${toe_client}/eric-oss-tosca-orchestration-enabler-client-${toe_client}.jar" \
    && cd ..  \
    && cp tmp_jars/eric-esoa-engine-delegates-${eric_eo_engine}.jar /flyway-${flyway_version}/jars/eric-esoa-engine-delegates-${eric_eo_engine}.jar

COPY docker/so-common-workflows-delegates/flyway.config flyway-${flyway_version}/conf/flyway.config

RUN chown $uid:$gid flyway-${flyway_version}/conf/flyway.config

USER $uid
# Run script for copy of jars and flyway migration
ENTRYPOINT ["sh", "/so-delegates-copy-jars-and-flyway-migrate.sh"]
