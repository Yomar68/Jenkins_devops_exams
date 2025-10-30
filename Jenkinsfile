pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_IMAGE = 'yomar68/jenkins-exam-app'
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/Yomar68/Jenkins_devops_exams.git', 
                    credentialsId: 'github-credentials'
            }
        }

        stage('Build') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_ID}", "cast-service")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'echo "Running tests..."'
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    docker.withRegistry('', 'dockerhub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_ID}").push()
                    }
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                script {
                    sh "kubectl config use-context minikube"
                    sh "kubectl get ns dev || kubectl create ns dev"
                    sh "kubectl apply -f k8s/dev/ -n dev"
                }
            }
        }

        stage('Deploy to QA') {
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main' }
            }
            steps {
                script {
                    sh "kubectl get ns qa || kubectl create ns qa"
                    sh "kubectl apply -f k8s/qa/ -n qa"
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main' }
            }
            steps {
                script {
                    sh "kubectl get ns staging || kubectl create ns staging"
                    sh "kubectl apply -f k8s/staging/ -n staging"
                }
            }
        }

        stage('Approve Production') {
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main' }
            }
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        input message: 'Déployer en production?', 
                              ok: 'Déployer en Production',
                              submitterParameter: 'APPROVER'
                    }
                }
            }
        }

        stage('Deploy to Production') {
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main' }
            }
            steps {
                script {
                    sh "kubectl get ns prod || kubectl create ns prod"
                    sh "helm upgrade --install jenkins-exam-app charts/ -n prod --set image.repository=${DOCKER_IMAGE} --set image.tag=${env.BUILD_ID}"
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminée'
            cleanWs()
        }
        success {
            echo 'Pipeline réussie!'
        }
        failure {
            echo 'Pipeline échouée!'
        }
    }
}
