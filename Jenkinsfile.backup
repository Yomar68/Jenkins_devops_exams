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

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh "kubectl config use-context minikube"

                    // Liste des environnements et chemins
                    def envs = ['dev', 'qa', 'staging', 'prod']
                    envs.each { e ->
                        // Crée le namespace s'il n'existe pas
                        sh "kubectl get ns ${e} || kubectl create ns ${e}"

                        // Applique les manifests
                        sh "kubectl apply -f k8s/${e}/ -n ${e}"
                    }
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

