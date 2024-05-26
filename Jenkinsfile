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
                git branch: 'main', credentialsId: 'jenkins_live', url: 'https://github.com/MohamedLong/xgarage.git'
                // checkout scmGit(
                //     branches: [[name: '*/main']],
                //     extensions: [cloneSubModules: true], // Clone submodules (if any)
                //     userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']]
                // )
            }
        }
        stage('Copy YAML Files from Branch') {
            steps {
                script {
                // Define paths (replace with actual values)
                def virtualServer = '192.168.100.10'
                def remoteDir = '/home/xgarage/public_html'

                // Define environment variable for the SSH key credential ID
                def sshKey = credentialsId('xgarage') // Assuming SSH key-based authentication

                // Existing code to get the list of YAML files (yamlFiles)

                // Access SSH key using 'withCredentials' block
                withCredentials([file(credentialsId: sshKey, variable: 'SSH_KEY_FILE')]) {

                    // Loop through each file and copy it to the virtual server
                    for (file in yamlFiles) {
                    def scpCommand = "scp -i ${env.SSH_KEY_FILE} ${file} ${virtualServer}:${remoteDir}"
                    sh scpCommand
                    }
                }
                }
            }
            // steps {
            //     script {
            //         // Define the destination directory
            //         def destinationDir = '/home/xgarage/public_html'

            //         // Use `sh` to get a list of YAML files in the branch
            //         def yamlFiles = sh(returnStdout: true, script: 'git ls-files "*.yaml"').split('\n')

            //         // Loop through each file and copy it to the destination
            //         for (file in yamlFiles) {
            //             sh "cp ${file} ${destinationDir}"
            //         }
            //     }
            // }
        }
    }
}
