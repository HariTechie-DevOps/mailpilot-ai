pipeline {
    agent any

    environment {
        // Path where your app lives on EC2
        APP_DIR = "/home/ubuntu/mailpilot"
        JAR_NAME = "mailpilot-1.0.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                // Pulls the code from your Git repo
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Compiles the code and creates the JAR
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    # 1. Stop the old app if it is running
                    pkill -f ${JAR_NAME} || true
                    
                    # 2. Copy the new JAR to your deployment folder
                    cp target/${JAR_NAME} ${APP_DIR}/
                    
                    # 3. Copy credentials if they aren't already there
                    cp src/main/resources/credentials.json ${APP_DIR}/
                    
                    # 4. Start the app in the background (nohup keeps it running after Jenkins finishes)
                    cd ${APP_DIR}
                    nohup java -jar ${JAR_NAME} > app.log 2>&1 &
                """
            }
        }
    }

    post {
        success {
            echo "Successfully deployed MailPilot to EC2!"
        }
        failure {
            echo "Deployment failed. Check Jenkins logs."
        }
    }
}
