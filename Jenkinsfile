pipeline {
    agent {
        label 'spring'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '1')) // Keeps the last 1 builds
    }

    stages {
        stage('Checkout Git Repository') {
            steps {
                checkout scmGit(
                    branches: [[name: '*/main']],
                    extensions: [cloneSubModules: true], // Clone submodules (if any)
                    userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']]
                )
            }
        }
        stage('Copy YAML Files from Branch') {
            steps {
                script {
                    // Define the destination directory
                    def destinationDir = '/home/xgarage/public_html'

                    // Use `sh` to get a list of YAML files in the branch
                    def yamlFiles = sh(returnStdout: true, script: 'git ls-files "*.yaml"').split('\n')

                    // Loop through each file and copy it to the destination
                    for (file in yamlFiles) {
                        sh "cp ${file} ${destinationDir}"
                    }
                }
            }
        }
    }
}
