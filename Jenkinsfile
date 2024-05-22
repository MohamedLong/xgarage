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
        stage('Copy yaml Files') {
            steps {
                sh 'cp dashboard-ingress.yaml /home/spring/k8s/'
                sh 'cp eureka-ingress.yaml /home/spring/k8s/'
                sh 'cp configMap.yaml /home/spring/k8s/'
                sh 'cp gateway_deployment.yaml /home/spring/k8s/'
                sh 'cp registry_deployment.yaml /home/spring/k8s/'
                sh 'cp kernal_deployment.yaml /home/spring/k8s/'
                sh 'cp core_deployment.yaml /home/spring/k8s/'
                sh 'cp web_deployment.yaml /home/spring/k8s/'
                
                
                
            }
        }
    }
    
}