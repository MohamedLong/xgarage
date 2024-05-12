pipeline {
    agent {
        label 'spring'
    }
 tools {
    maven '3.9.3'
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