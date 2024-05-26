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

            }
        }
        stage('Copy YAML Files from Branch') {
            steps {
                script {

                        withCredentials([usernamePassword(credentialsId: 'xgarage', usernameVariable: 'REMOTE_USER', passwordVariable: 'REMOTE_PASSWORD')]) {
                            def remoteHost = '192.168.100.10'
                            def remoteDir = '/home/xgarage/public_html'
        
                            // Copy Build files to the remote server
                            sh "sshpass -p '${env.REMOTE_PASSWORD}' scp -r /home/spring/workspace/xgarage_automation_main/* ${env.REMOTE_USER}@${remoteHost}:${remoteDir}"
        
                        }
                    
                    // // Define the destination directory
                    // def destinationDir = '/home/xgarage/public_html'

                    // // Use `sh` to get a list of YAML files in the branch
                    // def yamlFiles = sh(returnStdout: true, script: 'git ls-files "*.yaml"').split('\n')

                    // // Loop through each file and copy it to the destination
                    // for (file in yamlFiles) {
                    //     sh "cp ${file} ${destinationDir}"
                    // }
                }
            }
        }
    }
}
