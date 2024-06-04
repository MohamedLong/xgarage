// This Pipeline is used to generate Docker image for Eureka save it local of course then push the image to Docker hub 
pipeline {
    agent {
        label 'spring'
    }
    tools {
        maven '3.9.3'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '1')) 
    }

    stages {
        stage('Build Maven') {
           steps {
            checkout scmGit(branches: [[name: '*/registry']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']])
            sh "mvn clean install -DskipTests"
            
            }
            
        }
        stage('Build docker image'){
            steps{
                script{
                    sh 'docker build -t longali/registryservice .'
                }
            }
        }
        stage('Push image to Hub'){
            steps{
                script{
                  withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                  sh 'docker login -u longali -p ${dockerhubpwd}'
                  
                  sh 'docker push longali/registryservice'

                }
                }
            }
            
        }
    }
}