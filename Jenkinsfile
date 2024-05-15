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
            checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']])

            }
            
        }
        stage('Copy Ingress File') {
            steps {
                // Create the target directory if it doesn't exist
                // sh 'mkdir -p /home/spring/k8s'
                // Copy the dashboard-ingress.yaml file to the target directory
                sh 'cp dashboard-ingress.yaml /home/spring/k8s/'
            }
        }
    }
    
}