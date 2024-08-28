# Building so-common-workflows-bpmn docker image for development testing
Ensure to run the docker build command from the directory *docker/so-common-workflows-bpmn/* otherwise the command will fail to find the Dockerfile.

Run the following command to build the docker image with default arguments:

```
docker build . -t so-common-workflows-bpmn
```
**Note:** Use the *--no-cache* flag to prevent the image build using the cache.

# Running so-common-workflows-bpmn docker image

Run the following command to run the docker image with the appropriate values substituted:

```
docker run -e host=<PC host name> -e port=<port on which pc is running> -e tls.enabled=<is TLS enabled in PC> -e TLS_CERT_PATH=<tls certificate path> -v <local syatm tls path>:<tls certificate path> --rm -it <image>
```

e.g.
```
docker run -e host=xx.xx.xx.xx -e port=8443 -e tls.enabled=true -e TLS_CERT_PATH=/tmp/tls.crt -v /tmp:/tmp/ --rm -it so-common-workflows-bpmn
```
Below is a table explaining the values to be substituted:

| Environment variable | Value                                                                                 |
|----------------------|---------------------------------------------------------------------------------------|
| pc_host              | ingress host of the Process controller (Mandatory)                                    |
| pc_port              | port on which process controller is running (Mandatory)                               |
| pc_root_uri          | Root uri of process controller, default value is "process-controller/rest" (optional) |
| auth_host            | ingress host of the eoc admin (Mandatory)                                             |
| auth_port            | eoc admin service running port (Mandatory)                                            |
| username             | system username of the pc controller (Mandatory)                                      |
| password             | system password of the pc controller (Mandatory)                                      |
| max_retries          | max retry before which the request should fail (Optional)                             |
| delay                | delay in consecutive retries (Optional)                                               |
| connection_timeout   | Connection timeout, default value is 60 second (Optional)                             |
| tls_enabled          | Set to true if tls is enabled, default value is true. (Optional)                      |
| TLS_CERT_PATH        | mounted empty directory, default value is /tmp (Optional)                             |
| TLS_MNT_CERT_PATH    | mounted path to the cert (Optional)                                                   |
