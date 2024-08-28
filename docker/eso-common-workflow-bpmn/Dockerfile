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
ARG JAR_FILE
ARG COMMIT
ARG BUILD_DATE
ARG APP_VERSION
ARG user=common-workflow
ARG uid=146672
ARG gid=146672
ENV DEFAULT_JAVA_CACERTS="/usr/lib64/jvm/java-17-openjdk-17/lib/security/cacerts"

LABEL \
    adp.image.created=$BUILD_DATE \
    adp.revision=$COMMIT \
    adp.app.version=$APP_VERSION

COPY ./docker/eso-common-workflow-bpmn/eric-so-common-workflows-bpmn-jar-with-dependencies.jar /eric-so-common-workflows-bpmn.jar
COPY ./docker/eso-common-workflow-bpmn/entryPoint.sh /entryPoint.sh


RUN echo "${user}:x:${uid}:${gid}:UI-user:/:/bin/bash" >> /etc/passwd  \
    && sed -i 's/^\(root.*\):.*/\1:\/bin\/false/' /etc/passwd  \
    && sed -i 's/^root:/root:!/' /etc/shadow  \
    && chown $uid:$gid "${DEFAULT_JAVA_CACERTS}" && chmod +w "${DEFAULT_JAVA_CACERTS}" \
    && chmod +x eric-so-common-workflows-bpmn.jar \
    && chmod +x entryPoint.sh

USER $uid

ENTRYPOINT ["sh", "-c", "/entryPoint.sh $JAVA_OPTS"]


