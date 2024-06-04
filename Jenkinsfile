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
                git branch: 'docker-compose', credentialsId: 'jenkins_live', url: 'https://github.com/MohamedLong/xgarage.git'
            }
        }
        stage('Copy YAML Files from Branch') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'xgarage', usernameVariable: 'REMOTE_USER', passwordVariable: 'REMOTE_PASSWORD')]) {
                        def remoteHost = '192.168.100.10'
                        def remoteDir = '/home/xgarage/public_html'
                        def localDir = "${env.WORKSPACE}"

                        // Ensure sshpass is installed
                        // sh "sudo apt-get update && sudo apt-get install -y sshpass"

                        // Copy build files to the remote server
                        sh """
                            sshpass -p '${env.REMOTE_PASSWORD}' scp -o StrictHostKeyChecking=no -r ${localDir}/* ${env.REMOTE_USER}@${remoteHost}:${remoteDir}
                        """

                        // Run Docker Compose on the remote server
                        sh """
                            sshpass -p '${env.REMOTE_PASSWORD}' ssh -o StrictHostKeyChecking=no ${env.REMOTE_USER}@${remoteHost} 'cd ${remoteDir} && docker-compose up -d'
                        """
                    }
                }
            }
        }
    }
}
