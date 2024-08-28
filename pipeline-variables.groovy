/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
env.JAVA_VERSION="17"
env.BASE_IMAGE="SO_BASE"
env.DOCKER_BUILD_ARGS="eric_eo_engine:engine.version,eric_eo_workflow:workflow.version,domain_orchestrator_consumer:domain.orchestrator.consumer.version,domain_orchestrator:domain.orchestrator.version,error_message_factory:error.message.factory.version,toe_client:toe.client.version,toe_api:toe.api.version,eric_esoa_common_logging:eric.esoa.common.logging.version"
env.SKIP_LICENSE="true"