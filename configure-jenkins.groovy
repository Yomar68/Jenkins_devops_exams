import jenkins.model.*
import hudson.model.*
import hudson.tasks.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.plugins.git.*

// Créer un projet Freestyle
def job = Jenkins.instance.createProject(FreeStyleProject, "movie-app-pipeline")
job.description = "Pipeline CI/CD for movie application"

// Configuration Git
def scm = new GitSCM("https://github.com/Yomar68/Jenkins_devops_exams")
scm.branches = [new BranchSpec("*/main")]
job.scm = scm

// Commande de build
def command = '''echo "=== DÉMARRAGE DU PIPELINE ==="
echo "Build Number: \$BUILD_NUMBER"
docker build -t yomar68/movie-service:latest ./movie-service/
docker build -t yomar68/cast-service:latest ./cast-service/
docker images | grep yomar68
echo "=== PIPELINE TERMINÉ ==="'''

def builder = new Shell(command)
job.buildersList.add(builder)

job.save()
println "Projet créé avec succès!"
