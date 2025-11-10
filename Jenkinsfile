pipeline {
    agent any
    
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_IMAGE_MOVIE = "yomar68/movie-service"
        DOCKER_IMAGE_CAST = "yomar68/cast-service"
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }
    
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'qa', 'staging'],
            description: 'Environnement de déploiement'
        )
        booleanParam(
            name: 'DEPLOY_TO_PROD',
            defaultValue: false,
            description: 'Déploiement manuel en production'
        )
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Images') {
            parallel {
                stage('Build Movie Service') {
                    steps {
                        sh '''
                            echo "Construction de movie-service"
                            docker build -t $DOCKER_IMAGE_MOVIE:$BUILD_NUMBER ./movie-service/
                            docker tag $DOCKER_IMAGE_MOVIE:$BUILD_NUMBER $DOCKER_IMAGE_MOVIE:latest
                        '''
                    }
                }
                stage('Build Cast Service') {
                    steps {
                        sh '''
                            echo "Construction de cast-service"
                            docker build -t $DOCKER_IMAGE_CAST:$BUILD_NUMBER ./cast-service/
                            docker tag $DOCKER_IMAGE_CAST:$BUILD_NUMBER $DOCKER_IMAGE_CAST:latest
                        '''
                    }
                }
            }
        }
        
        stage('Push to DockerHub') {
            steps {
                sh '''
                    docker login -u $DOCKERHUB_CREDENTIALS_USR -p $DOCKERHUB_CREDENTIALS_PSW
                    echo "Pushing movie-service"
                    docker push $DOCKER_IMAGE_MOVIE:$BUILD_NUMBER
                    docker push $DOCKER_IMAGE_MOVIE:latest
                    echo "Pushing cast-service"
                    docker push $DOCKER_IMAGE_CAST:$BUILD_NUMBER
                    docker push $DOCKER_IMAGE_CAST:latest
                '''
            }
        }
        
        stage('Deploy to Environment') {
            steps {
                script {
                    if (params.DEPLOY_TO_PROD && env.BRANCH_NAME == 'main') {
                        echo "🚀 DÉPLOIEMENT PRODUCTION MANUEL"
                        sh """
                            kubectl get ns prod || kubectl create ns prod
                            # Nettoyer les déploiements existants de manière plus robuste
                            helm uninstall movie-service -n prod 2>/dev/null || true
                            helm uninstall cast-service -n prod 2>/dev/null || true
                            kubectl delete deployment movie-service -n prod --ignore-not-found=true --wait=false
                            kubectl delete service movie-service -n prod --ignore-not-found=true --wait=false
                            kubectl delete deployment cast-service -n prod --ignore-not-found=true --wait=false
                            kubectl delete service cast-service -n prod --ignore-not-found=true --wait=false
                            # Attendre que les ressources soient supprimées
                            sleep 15
                            # Déployer avec --force pour remplacer les ressources existantes
                            helm upgrade --install movie-service ./k8s-charts/movie-service -n prod --set image.repository=$DOCKER_IMAGE_MOVIE --set image.tag=latest --force --timeout 5m
                            helm upgrade --install cast-service ./k8s-charts/cast-service -n prod --set image.repository=$DOCKER_IMAGE_CAST --set image.tag=latest --force --timeout 5m
                        """
                    } else if (params.ENVIRONMENT != 'prod') {
                        echo "Déploiement vers l'environnement ${params.ENVIRONMENT}"
                        sh """
                            kubectl get ns ${params.ENVIRONMENT} || kubectl create ns ${params.ENVIRONMENT}
                            # Nettoyer les déploiements existants de manière plus robuste
                            helm uninstall movie-service -n ${params.ENVIRONMENT} 2>/dev/null || true
                            helm uninstall cast-service -n ${params.ENVIRONMENT} 2>/dev/null || true
                            kubectl delete deployment movie-service -n ${params.ENVIRONMENT} --ignore-not-found=true --wait=false
                            kubectl delete service movie-service -n ${params.ENVIRONMENT} --ignore-not-found=true --wait=false
                            kubectl delete deployment cast-service -n ${params.ENVIRONMENT} --ignore-not-found=true --wait=false
                            kubectl delete service cast-service -n ${params.ENVIRONMENT} --ignore-not-found=true --wait=false
                            # Attendre que les ressources soient supprimées
                            sleep 15
                            # Déployer avec --force pour remplacer les ressources existantes
                            helm upgrade --install movie-service ./k8s-charts/movie-service -n ${params.ENVIRONMENT} --set image.repository=$DOCKER_IMAGE_MOVIE --set image.tag=latest --force --timeout 5m
                            helm upgrade --install cast-service ./k8s-charts/cast-service -n ${params.ENVIRONMENT} --set image.repository=$DOCKER_IMAGE_CAST --set image.tag=latest --force --timeout 5m
                        """
                    } else {
                        echo "❌ Déploiement en production non autorisé depuis cette branche"
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    if (params.DEPLOY_TO_PROD && env.BRANCH_NAME == 'main') {
                        sh """
                            echo "Vérification du déploiement en production..."
                            kubectl get pods -n prod
                            kubectl get svc -n prod
                        """
                    } else if (params.ENVIRONMENT != 'prod') {
                        sh """
                            echo "Vérification du déploiement en ${params.ENVIRONMENT}..."
                            kubectl get pods -n ${params.ENVIRONMENT}
                            kubectl get svc -n ${params.ENVIRONMENT}
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh "echo 'Pipeline terminé - Build: $BUILD_NUMBER - Environnement: ${params.ENVIRONMENT}'"
            cleanWs()
        }
        success {
            sh 'echo "✅ SUCCÈS: Pipeline terminé avec succès"'
        }
        failure {
            sh 'echo "❌ ÉCHEC: Pipeline a échoué"'
        }
    }
}
