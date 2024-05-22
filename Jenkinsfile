pipeline {
    agent {
        label 'spring'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '1')) // Keeps the last 1 builds
    }

    stages {
        stage('Checkout and Build Angular') {
           steps {
            checkout scmGit(branches: [[name: '*/angular']], extensions: [], userRemoteConfigs: [[credentialsId: 'github', url: 'https://github.com/MohamedLong/xgarage.git']])
            sh "npm install --legacy-peer-deps"
            
            }
            
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t longali/xgarageangular .'
                }
            }
        }
        stage('Push Image to Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                        sh 'docker login -u longali -p ${dockerhubpwd}'
                        sh 'docker push longali/xgarageangular'
                    }
                }
            }
        }
    }
}
