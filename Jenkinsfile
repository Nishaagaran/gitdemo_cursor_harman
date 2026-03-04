// Banking API CI/CD Pipeline
// Jenkins setup: Configure "JDK17" (Java 17) and "Maven 3.9" in Manage Jenkins -> Tools.
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
        JAVA_HOME = "${tool 'JDK17'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = env.GIT_COMMIT?.take(7) ?: 'unknown'
                    env.BUILD_DISPLAY_NAME = "#${env.BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        stage('Build') {
            steps {
                withMaven(maven: 'Maven 3.9') {
                    sh 'mvn clean compile -B -DskipTests'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                withMaven(maven: 'Maven 3.9') {
                    sh 'mvn test -B'
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
            }
        }

        stage('Package') {
            steps {
                withMaven(maven: 'Maven 3.9') {
                    sh 'mvn package -B -DskipTests'
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                fingerprint: true,
                                allowEmptyArchive: false
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
                script {
                    def jarFile = findFiles(glob: 'target/*.jar')[0]
                    echo "Deploying ${jarFile?.name} to staging environment..."
                    // Replace with actual deployment (e.g. SSH, kubectl, Docker push, or copy to server)
                    // deployToStaging(jarFile.name)
                    sh """
                        echo "Staging deployment placeholder for ${jarFile?.name}"
                        echo "STAGING_URL=\${STAGING_URL:-http://staging.example.com}" 
                    """
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
                script {
                    echo 'Running integration tests against staging...'
                    // Replace with real integration test runner (e.g. REST Assured, Postman, or custom script)
                    // sh 'mvn verify -P integration-tests'
                    sh 'echo "Integration tests placeholder; add your test command here."'
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
        }

        stage('Deploy to Production') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    def jarFile = findFiles(glob: 'target/*.jar')[0]
                    echo "Deploying ${jarFile?.name} to production..."
                    // Replace with actual production deployment
                    // deployToProduction(jarFile.name)
                    sh """
                        echo "Production deployment placeholder for ${jarFile?.name}"
                        echo "PRODUCTION_URL=\${PRODUCTION_URL:-http://prod.example.com}"
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
            // Optional: slackSend(color: 'good', message: "Build ${env.BUILD_NUMBER} succeeded")
        }
        failure {
            echo 'Pipeline failed.'
            // Optional: slackSend(color: 'danger', message: "Build ${env.BUILD_NUMBER} failed")
        }
        always {
            cleanWs(deleteDirs: true, patterns: [[pattern: '.m2/**', type: 'INCLUDE']])
        }
    }
}
