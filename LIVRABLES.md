# LIVRABLES EXAMEN JENKINS DEVOPS

## Informations de livraison

### 1. Dépôt GitHub
- URL: https://github.com/Yomar68/Jenkins_devops_exams

### 2. DockerHub
- Username: yomar68
- Images déployées:
  - yomar68/movie-service:latest
  - yomar68/cast-service:latest

### 3. Jenkins
- URL: http://34.242.178.245:32457
- Username: admin
- Password: admin

### 4. Environnements Kubernetes
- Namespaces créés: dev, qa, staging, prod
- Applications déployées dans: dev

## Architecture déployée
- 2 microservices FastAPI: movie-service et cast-service
- 2 bases de données PostgreSQL
- Reverse Proxy Nginx (à déployer)
- Jenkins pour l'automatisation CI/CD

## Prochaines étapes
1. Configuration des credentials DockerHub dans Jenkins
2. Création des pipelines multi-environnements
3. Configuration du déploiement manuel en production
4. Tests et validation
