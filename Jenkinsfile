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
                    // Build Docker image depuis le dossier cast-service
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
                    sh "kubectl apply -f k8s/dev/ -n dev"
                }
            }
        }

        stage('Deploy to QA') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh "kubectl apply -f k8s/qa/ -n qa"
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh "kubectl apply -f k8s/staging/ -n staging"
                }
            }
        }

        stage('Approve Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        input message: 'Déployer en production?', ok: 'Déployer'
                    }
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh "kubectl apply -f k8s/prod/ -n prod"
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

