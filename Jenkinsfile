pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    jenkins-agent: maven-docker-pod
spec:
  containers:
  - name: maven
    image: maven:3.9.5-eclipse-temurin-17-alpine
    command:
    - cat
    tty: true
    volumeMounts:
    - name: maven-cache
      mountPath: /root/.m2
  - name: docker
    image: docker:24.0.6-dind
    securityContext:
      privileged: true
    env:
    - name: DOCKER_TLS_CERTDIR
      value: ""
  - name: grype
    image: anchore/grype:latest
    command:
    - cat
    tty: true
  - name: helm
    image: alpine/helm:3.13.2
    command:
    - cat
    tty: true
  volumes:
  - name: maven-cache
    persistentVolumeClaim:
      claimName: maven-cache-pvc
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
"""
        }
    }
    
    environment {
        // Docker image details
        DOCKER_IMAGE = 'demo-pipeline'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        
        // SonarQube configuration
        SONAR_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'demo-pipeline'
        
        // Kubernetes namespace
        K8S_NAMESPACE = 'demo'
        
        // Registry credentials
        DOCKER_REGISTRY_CREDENTIAL = 'docker-registry-credentials'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build & Test') {
            steps {
                container('maven') {
                    sh '''
                    mvn clean package
                    '''
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                container('maven') {
                    withSonarQubeEnv('SonarQube') {
                        sh """
                        mvn sonar:sonar \\
                          -Dsonar.projectKey=\${SONAR_PROJECT_KEY} \\
                          -Dsonar.host.url=\${SONAR_URL}
                        """
                    }
                }
            }
        }
        
        stage('SonarQube Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                container('docker') {
                    sh """
                    docker build -t \${DOCKER_IMAGE}:\${DOCKER_TAG} .
                    """
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                container('grype') {
                    sh """
                    grype \${DOCKER_IMAGE}:\${DOCKER_TAG} --fail-on high
                    """
                }
            }
        }
        
        stage('Push Image') {
            steps {
                container('docker') {
                    withCredentials([usernamePassword(credentialsId: DOCKER_REGISTRY_CREDENTIAL, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh """
                        echo \${DOCKER_PASSWORD} | docker login -u \${DOCKER_USERNAME} --password-stdin
                        docker tag \${DOCKER_IMAGE}:\${DOCKER_TAG} \${DOCKER_USERNAME}/\${DOCKER_IMAGE}:\${DOCKER_TAG}
                        docker push \${DOCKER_USERNAME}/\${DOCKER_IMAGE}:\${DOCKER_TAG}
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                container('helm') {
                    sh """
                    helm upgrade --install demo-pipeline helm/demo-pipeline \\
                      --set image.tag=\${DOCKER_TAG} \\
                      --namespace \${K8S_NAMESPACE} \\
                      --create-namespace
                    """
                }
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            jacoco(
                execPattern: '**/target/jacoco.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                exclusionPattern: '**/src/test*'
            )
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
