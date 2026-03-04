// Banking API CI/CD Pipeline
// Optional: Configure "JDK17" (Java 17) and "Maven 3.9" in Manage Jenkins -> Tools for consistent builds.
// If not set, the pipeline uses the agent's Java and Maven from PATH.
// For production/staging, set STAGING_URL and PRODUCTION_URL and replace placeholder deploy steps.

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository -Xmx1024m'
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    try {
                        env.JAVA_HOME = tool 'JDK17'
                        echo "Using JDK17 from Jenkins tools: ${env.JAVA_HOME}"
                    } catch (Exception e) {
                        echo "JDK17 not configured; using agent's default Java. Add JDK17 in Manage Jenkins -> Tools for consistent builds."
                    }
                    try {
                        def mavenPath = tool 'Maven 3.9'
                        env.PATH = isUnix() ? "${mavenPath}/bin:${env.PATH}" : "${mavenPath}\\bin;${env.PATH}"
                        echo "Using Maven 3.9 from Jenkins tools"
                    } catch (Exception e) {
                        echo "Maven 3.9 not configured; using Maven from PATH. Add in Manage Jenkins -> Tools if build fails."
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                retry(2) {
                    checkout scm
                }
                script {
                    env.GIT_COMMIT_SHORT = env.GIT_COMMIT?.take(7) ?: 'unknown'
                    env.BUILD_DISPLAY_NAME = "#${env.BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Checkout"
                        echo "Checkout failed after retries. Check repository URL and credentials."
                    }
                }
            }
        }

        stage('Build') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        if (isUnix()) {
                            sh 'mvn clean compile -B -DskipTests'
                        } else {
                            bat 'mvn clean compile -B -DskipTests'
                        }
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Build"
                        echo "Build failed. Check compile errors and Maven/Java configuration."
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    script {
                        if (isUnix()) {
                            sh 'mvn test -B'
                        } else {
                            bat 'mvn test -B'
                        }
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: '*.html',
                        reportName: 'Unit Test Report',
                        reportTitles: ''
                    ])
                }
                failure {
                    script {
                        currentBuild.description = "Failed at Unit Tests"
                        echo "Unit tests failed. Review test report and fix failing tests."
                    }
                }
            }
        }

        stage('Package') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        if (isUnix()) {
                            sh 'mvn package -B -DskipTests'
                        } else {
                            bat 'mvn package -B -DskipTests'
                        }
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Package"
                        echo "Packaging failed. Ensure Build and Unit Tests passed."
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                script {
                    try {
                        def files = findFiles(glob: 'target/*.jar')
                        if (files.length == 0) {
                            error("No JAR found in target/. Package stage may have failed or produced no artifact.")
                        }
                        archiveArtifacts artifacts: 'target/*.jar',
                                        fingerprint: true,
                                        allowEmptyArchive: false
                    } catch (Exception e) {
                        error("Archive failed: ${e.message}")
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Archive Artifacts"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                retry(2) {
                    timeout(time: 5, unit: 'MINUTES') {
                        script {
                            try {
                                def jarFile = findFiles(glob: 'target/*.jar')[0]
                                echo "Deploying ${jarFile?.name} to staging environment..."
                                // Replace with actual deployment (e.g. SSH, kubectl, Docker push, or copy to server)
                                sh """
                                    echo "Staging deployment placeholder for ${jarFile?.name}"
                                    echo "STAGING_URL=\${STAGING_URL:-http://staging.example.com}"
                                """
                            } catch (Exception e) {
                                error("Staging deploy failed: ${e.message}")
                            }
                        }
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Deploy to Staging"
                    }
                }
            }
        }

        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        try {
                            echo 'Running integration tests against staging...'
                            // Replace with real integration test runner (e.g. REST Assured, Postman, or custom script)
                            sh 'echo "Integration tests placeholder; add your test command here."'
                        } catch (Exception e) {
                            error("Integration tests failed: ${e.message}")
                        }
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Integration Tests"
                    }
                }
            }
        }

        stage('Approve Production') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                input message: 'Deploy to production?',
                      ok: 'Deploy',
                      submitter: 'admin,release-managers'
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Production deployment skipped or rejected"
                    }
                }
            }
        }

        stage('Deploy to Production') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                retry(2) {
                    timeout(time: 10, unit: 'MINUTES') {
                        script {
                            try {
                                def jarFile = findFiles(glob: 'target/*.jar')[0]
                                if (jarFile == null) {
                                    error("No JAR artifact found for production deployment.")
                                }
                                echo "Deploying ${jarFile.name} to production..."
                                // Replace with actual production deployment
                                sh """
                                    echo "Production deployment placeholder for ${jarFile.name}"
                                    echo "PRODUCTION_URL=\${PRODUCTION_URL:-http://prod.example.com}"
                                """
                            } catch (Exception e) {
                                error("Production deploy failed: ${e.message}")
                            }
                        }
                    }
                }
            }
            post {
                failure {
                    script {
                        currentBuild.description = "Failed at Deploy to Production"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully. Build #${env.BUILD_NUMBER}"
            // Optional: slackSend(color: 'good', message: "Build ${env.BUILD_NUMBER} succeeded")
        }
        failure {
            script {
                def msg = currentBuild.description ?: "Pipeline failed at an unspecified stage."
                echo "Pipeline failed: ${msg}"
                // Optional: slackSend(color: 'danger', message: "Build ${env.BUILD_NUMBER} failed: ${msg}")
            }
        }
        unstable {
            echo "Pipeline marked unstable (e.g. test failures or unstable step)."
        }
        aborted {
            echo "Pipeline aborted (e.g. manual abort or approval rejected)."
        }
        always {
            script {
                // cleanWs requires FilePath (node context). Run inside node to avoid MissingContextVariableException when post runs after early failure.
                node {
                    cleanWs(deleteDirs: true, patterns: [[pattern: '.m2/**', type: 'INCLUDE']])
                }
            }
        }
    }
}
