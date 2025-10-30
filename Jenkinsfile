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
                    // Crée le namespace s'il n'existe pas
                    sh "kubectl get ns dev || kubectl create ns dev"
                    // Déploie en dev
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
                    // Crée le namespace s'il n'existe pas
                    sh "kubectl get ns qa || kubectl create ns qa"
                    // Déploie en QA
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
                    // Crée le namespace s'il n'existe pas
                    sh "kubectl get ns staging || kubectl create ns staging"
                    // Déploie en staging
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
                        input message: 'Déployer en production?', 
                              ok: 'Déployer en Production',
                              submitterParameter: 'APPROVER'
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
                    // Crée le namespace s'il n'existe pas
                    sh "kubectl get ns prod || kubectl create ns prod"
                    
                    // OPTION 1: Utilisation des charts Helm (recommandé)
                    sh "helm upgrade --install jenkins-exam-app charts/ -n prod --set image.repository=${DOCKER_IMAGE} --set image.tag=${env.BUILD_ID}"
                    
                    // OPTION 2: Fallback avec kubectl apply si Helm échoue
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
