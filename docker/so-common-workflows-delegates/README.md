# Building so-common-workflows-delegates docker image for development testing
Ensure to run the docker build command from the directory *docker/so-common-workflows-delegates/* otherwise the command will fail to find the Dockerfile.

Run the following command to build the docker image with default arguments:

```
docker build . -t so-common-workflows-delegates
```
**Note:** Use the *--no-cache* flag to prevent the image build using the cache.

To update the jar versions to be used, use the *--build-arg* flag when running the build command:
```
docker build --build-arg <argument>=<value> . -t so-common-workflows-delegates
```
e.g.
```
docker build --build-arg eric_eo_engine=3.0.6-1 . -t so-common-workflows-delegates
```
**Note:** See the Dockerfile for the list of ARG values that can be updated. The *--build-arg* flag must be provided for each argument to be updated.

# Running so-common-workflows-delegates docker image

Run the following command to run the docker image with the appropriate values substituted:

```
docker run -e POSTGRES_USERNAME=<postgres username> -e POSTGRES_PASSWORD=<postgres password> -e POSTGRES_URL=<postgres url> -e SO_DELEGATES_PATH=<delegates path> -v <target directory>:<delegates path> --rm -it <image>
```

e.g.
```
docker run -e POSTGRES_USERNAME=pg_user -e POSTGRES_PASSWORD=pg_password -e POSTGRES_URL=jdbc:postgresql://localhost:5432/test_db -e SO_DELEGATES_PATH=/copied_jars -v $(pwd)/jars:/copied_jars --rm -it so-common-workflows-delegates
```
Below is a table explaining the values to be substituted:

| Environment variable | Value                                                                                                                                             |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| POSTGRES_USERNAME    | The username of the postgres database, e.g. pg_user (Mandatory)                                                                                   |
| POSTGRES_PASSWORD    | The password of the postgres database, e.g. pg_password (Mandatory)                                                                               |
| POSTGRES_URL         | The url to connect to the postgres database which should include the name of the table, e.g. jdbc:postgresql://172.0.0.1:5432/test_pg (Mandatory) |
| SO_DELEGATES_PATH    | The path to mount the volume to where the delegate jars are to be stored, e.g. /test_directory (Optional - see below note)                        |

**Note:** If the SO_DELEGATES_PATH environment variable is not provided, the SO_DELEGATES_PATH will be set by default to */tmp* and */tmp* must be used as the destination path for the volume when running the docker image:

```
docker run -e POSTGRES_USERNAME=pg_user -e POSTGRES_PASSWORD=pg_password -e POSTGRES_URL=jdbc:postgresql://localhost:5432/test_db -v $(pwd)/jars:/tmp --rm -it so-common-workflows-delegates
```

**Note:** The volume passed in as part of the command should contain the local directory where the jars should be stored and the mounted directory specified by the SO_DELEGATES_PATH environment variable, e.g.:

```
-v /path/to/local/directory:/test_directory
```
