FROM qa.stratio.com/stratio/stratio-base:2.0.0

# ***************************************************************
#   Set Maintainer
# ***************************************************************
MAINTAINER Command Center Team - StratioBD <command-center@stratio.com>

# ***************************************************************
#   Modules Version Variables
# ***************************************************************
ENV KMS_UTILS_VERSION 0.4.7
ENV B_LOG_VERSION 0.4.7
ENV JAVA_VERSION 11

# ***************************************************************
#   Environment Variables
# ***************************************************************
ENV TOOLS_PATH /tools

# Java environment variables
ENV LD_LIBRARY_PATH /usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64/jre/lib/amd64/server:$LD_LIBRARY_PATH
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# ***************************************************************
#   Operative System dependencies and configuration
# ***************************************************************

# Install basic tools
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        #Dockerfile
        wget \
        #kms_utils.sh
        curl \
        jq \
        #Architecture
        ca-certificates \
        vim \
        #Java Applications
        openjdk-${JAVA_VERSION}-jre \
        # Install Locales Binary
        locales \
        # Install external repositories
        apt-utils \
    && apt-get -y autoremove --purge \
    && rm -rf /tmp/* /var/tmp/* /var/lib/apt/lists/*

# ***************************************************************
#   Generate locales and set Default Language
# ***************************************************************
RUN cp /usr/share/i18n/SUPPORTED /etc/locale.gen \
    && locale-gen

ENV LC_ALL ${DEFAULT_LANG}
ENV LANG ${DEFAULT_LANG}
ENV LANGUAGE ${DEFAULT_LANG}

# ***************************************************************
#   Generate tools path
# ***************************************************************
RUN mkdir -p $TOOLS_PATH 

# ***************************************************************
#   Install kms library
# ***************************************************************
ADD http://niquel.stratio.com/repository/paas/kms_utils/${KMS_UTILS_VERSION}/kms_utils-${KMS_UTILS_VERSION}.sh $TOOLS_PATH/kms_utils.sh

# ***************************************************************
#   Install b-log library
# ***************************************************************
ADD http://niquel.stratio.com/repository/paas/log_utils/${B_LOG_VERSION}/b-log-${B_LOG_VERSION}.sh $TOOLS_PATH/b-log.sh

# ***************************************************************
#   Add volume
# ***************************************************************
#VOLUME /tmp

# ***************************************************************
#   Add entrypoint
# ***************************************************************
ADD src/docker/entrypoint.sh /data/entrypoint.sh

# ***************************************************************
#   Add execute permission to entypoint
# ***************************************************************
RUN chmod 600 /data/entrypoint.sh

# ***************************************************************
#   Set Artifact Version
# ***************************************************************
ARG VERSION=0.1.0-SNAPSHOT

# ***************************************************************
#   Add jar
# ***************************************************************
ADD cct-k8s-events-producer/target/cct-k8s-events-producer-${VERSION}-exec.jar /data/app.jar

# ***************************************************************
#   Add user
# ***************************************************************
ENV USER appuser
RUN useradd -s /bin/bash -M $USER \
    && chown -R $USER /data \
    && chown -R $USER $TOOLS_PATH \
    && chmod -R 700 /data \
    && chown $USER $JAVA_HOME/lib/security/cacerts
 USER $USER

# ***************************************************************
#   Entrypoint
# ***************************************************************
ENTRYPOINT ["bash", "/data/entrypoint.sh" ]
