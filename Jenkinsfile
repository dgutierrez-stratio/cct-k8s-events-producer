@Library('libpipelines@master') _

hose {
    EMAIL = 'command-center'

    DEVTIMEOUT = 60
    RELEASETIMEOUT = 60

    BUILDTOOLVERSION = '3.5.0'
    NEW_VERSIONING = true
    ANCHORE_POLICY = "production"

    DEV = { config ->
        doCompile(config)
        doUT(conf: config)
        doIT(conf: config)
        doPackage(config)
        doDeploy(config)
        doDocker(config)
    }

    INSTALLSERVICES = [
        ['CHROME': [
            'image': 'selenium/node-chrome-debug:3.9.1',
            'volumes': ['/dev/shm:/dev/shm'],
            'env': ['HUB_HOST=selenium391.cd','HUB_PORT=4444','SE_OPTS="-browser browserName=chrome,version=64%%JUID "']
            ]
        ],
        ['DCOSCLI':   ['image': 'stratio/dcos-cli:0.4.15-SNAPSHOT',
                           'env':     ['DCOS_IP=\$DCOS_IP',
                                      'SSL=true',
                                      'SSH=true',
                                      'TOKEN_AUTHENTICATION=true',
                                      'DCOS_USER=\$DCOS_USER',
                                      'DCOS_PASSWORD=\$DCOS_PASSWORD',
                                      'CLI_BOOTSTRAP_USER=\$CLI_BOOTSTRAP_USER',
                                      'CLI_BOOTSTRAP_PASSWORD=\$CLI_BOOTSTRAP_PASSWORD'],
                           'sleep':  120,
                           'healthcheck': 5000]]
    ]

    ATCREDENTIALS = [[TYPE:'sshKey', ID:'PEM_VMWARE']]

    INSTALLPARAMETERS = """
            | -DDCOS_CLI_HOST=%%DCOSCLI#0
            | -DREMOTE_USER=\$PEM_VMWARE_USER
            | -DPEM_FILE_PATH=\$PEM_VMWARE_KEY
            | -DSELENIUM_GRID=selenium391.cd:4444
            | -DFORCE_BROWSER=chrome_64%%JUID
            | -DPGC_CPU=1
            | -DPGC_MEMORY=1024
            | -DPGC_DISK_SPACE=1024
            """

    INSTALL = { config ->
        if (config.INSTALLPARAMETERS.contains('GROUPS_CCT')) {
            config.INSTALLPARAMETERS = "${config.INSTALLPARAMETERS}".replaceAll('-DGROUPS_CCT', '-Dgroups')
          doAT(conf: config)
        } else {
            doAT(conf: config, groups: ['cct_nightly'])
        }
     }
}
