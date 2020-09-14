# cct-k8s-events-producer

## Run on local

```sh
./mvnw -f ./cct-k8s-events-producer/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```
By default, these profiles start the service with:
* A stubbed tenant resolved. By default will be resolved with tenant NONE
* Debug mode

### Enable remote debugging

To enable the remote debugging of the service, execute:

```sh
./mvnw -f ./cct-k8s-events-producer/pom.xml spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

Thus, the remote debugging will be enabled through the port 5005.

## Docker Build

```sh
./mvnw install -P docker
```

## Service Properties

### Stratio Security Web Properties

| Key | Default Value | Description |
|-----|---------------|-------------|
| stratio.security.web.enabled | true | Enables secured Tomcat. The application will use an improved Tomcat with mutual TLS enabled (true\false) |
| stratio.security.vault.enabled | true | Enables Vault. Allows the interaction with vault though a VaultTemplate (true\false) |

See [Stratio Security Web Autoconfigure Readme][stratio_security_web_autoconfigure_readme] for more info.

### Stratio Security Tools Properties

| Key | Default Value | Description |
|-----|---------------|-------------|
| stratio.security.whitelist.enabled | false | Enables whitelist for authorized request using Spring Security (true\false) |
| stratio.security.whitelist.authorized-list | | Comma separated list of the authorized CNs (Common Names) |
| stratio.security.jwt.enabled | true | Enables the JWT security filter (true\false) |
| stratio.security.jwt.patterns | * | Comma separated list of the endpoints to be securized |
| stratio.security.jwt.validate | true | Enables the validation against Gosec for the JWT sign |
| stratio.security.jwt.secret.path | /dcs/passwords/oauth/jwtsecret | Path in Vault where the token signature is validated |
| stratio.security.jwt.secret.name | /dcs/passwords/oauth/jwtsecret | Name of the secret in Vault where the token signature is validated |

See [Stratio Security Tools Autoconfigure Readme][stratio_security_tools_autoconfigure_readme] for more info.

### Application Properties

| Key | Default Value | Description |
|-----|---------------|-------------|
| application.tenant-resolver.type | STUB | Tenant resolver implementation to use (GOSEC\|STUB) |
| application.tenant-resolver.stub.default-tenant | NONE | The default tenant to return for the tenant resolver stub |
| application.torreznos-stock-persistence.type | IN\_MEMORY | Torreznos stock persistence port implementation to use (IN_MEMORY/|JPA) |

## Acceptance Tests

The acceptance tests consist on Cucumber automated tests.

This module depends on a QA library (stratio-test-bdd), where common logic and steps are implemented.
 
### Execution

These tests will be executed as part of the continuous integration flow as follows:

```sh
./mvnw -f ./cct-k8s-events-producer-test/cct-k8s-events-producer-acceptance-tests/pom.xml verify [-D\<ENV_VAR>=\<VALUE>] [-Dit.test=\<TEST_TO_EXECUTE>|-Dgroups=\<GROUP_TO_EXECUTE>]
```

For example:

```sh
./mvnw -f ./cct-k8s-events-producer-test/cct-k8s-events-producer-acceptance-tests/pom.xml clean verify -DDCOS_IP=#DCOS_IP# -DBOOTSTRAP_IP=#BOOTSTRAP_IP# -DDCOS_CLI_HOST=#DCOS_CLI_HOST# -DDCOS_CLI_USER=#DCOS_CLI_USER# -DDCOS_CLI_PASSWORD=#DCOS_CLI_PASSWORD# -DREMOTE_USER=#REMOTE_USER# -DPEM_FILE_PATH=#PEM_FILE_PATH# -DSTRATIO_COMMAND_CENTER=0.11.0 -DDCOS_TENANT=#DCOS_TENANT# -DDCOS_PASSWORD=#DCOS_PASSWORD#
```

#### Properties

* DCOS_IP: IP from the cluster
* BOOTSTRAP_IP: IP of the bootstrap system in the cluster
* DCOS\_CLI_HOST: name/IP of the dcos-cli docker container
* DCOS\_CLI_USER: dcos-cli docker user
* DCOS\_CLI_PASSWORD: dcos-cli docker password
* DCOS_PASSWORD: DCOS cluster password
* DCOS_TENANT: DCOS cluster tenant
* REMOTE_USER: operational user for cluster machines
* PEM\_FILE_PATH: local path to pem file for cluster machines
* STRATIO\_COMMAND_CENTER: API version

#### Run DCOS client

```sh
docker run -it -v #PEM_FILE_PATH#:/tmp/testkeypair.pem:ro --name dcos-cli -e DCOS_IP=#DCOS_IP# -e SSL=true -e SSH=true -e TOKEN_AUTHENTICATION=true -e DCOS_USER=#DCOS_USER# -e DCOS_PASSWORD=#DCOS_PASSWORD# -e BOOTSTRAP_USER=operador -e PEM_FILE_PATH=/tmp/testkeypair.pem qa.stratio.com/stratio/dcos-cli:0.4.15
```

[stratio_security_web_autoconfigure_readme]: https://github.com/Stratio/stratio-microservices/blob/master/stratio-microservices-autoconfigure/stratio-security-web-autoconfigure/README.md
[stratio_security_tools_autoconfigure_readme]: https://github.com/Stratio/stratio-microservices/blob/master/stratio-microservices-autoconfigure/stratio-security-tools-autoconfigure/README.md