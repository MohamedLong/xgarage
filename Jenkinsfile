pipeline {
    agent {
        label 'spring'
    }

    environment {
        DOCKER_IMAGE = 'longali/xgarageangular'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '1')) // Keeps the last 1 builds
    }

    stages {
        stage('Checkout and Build Angular') {
           steps {
            // checkout scmGit(branches: [[name: '*/angular']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']])
            checkout scmGit(branches: [[name: '*/angular']], extensions: [], userRemoteConfigs: [[credentialsId: 'github', url: 'https://github.com/MohamedLong/xgarage.git']])
            sh "npm install --legacy-peer-deps"
            
            }
            
        }

        // stage('Checkout and Build Angular') {
        //     steps {
        //         checkout([$class: 'GitSCM',
        //                 branches: [[name: '*/angular']],
        //                 userRemoteConfigs: [[url: 'https://github.com/MohamedLong/xgarage.git']]])
        //         // Install npm packages
        //         sh "npm install --legacy-peer-deps"
        //         sh "npm run build"
        //     }
        // }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build(env.DOCKER_IMAGE)
                }
            }
        }
        stage('Push Image to Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'dockerhubpwd')]) {
                        sh 'docker login -u longali -p ${dockerhubpwd}'
                            docker.image(env.DOCKER_IMAGE).push('latest')
                    }
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                // Add your Kubernetes deployment script here
            }
        }
    }
}


// pipeline {
    
//     agent {
//         label 'spring'
//     }

//     stages {
//         stage('Checkout') {
//             steps {
//                 // Checkout your Angular project from Git
//                 git branch: 'angular', credentialsId: 'after_error', url: 'https://github.com/MohamedLong/xgarage.git'
//                 // Install npm packages
//                 sh "npm install --legacy-peer-deps"
//             }
//         }

//         stage('Build Angular App') {
//             steps {
//                 // Build the Angular project
//                 script {
//                     sh "npm run build"
//                 }
//             }
//         }

//     }


// }