pipeline {
    agent any

    environment {
        GH_USERNAME     = credentials('gh-user')
        GH_TOKEN        = credentials('gh-token')
        DOCKER_PASSWD   = credentials('lab-docker-passwd')
    }

    parameters {
        string(
            name: 'EXTRA_OPTS',
            defaultValue: '--no-build-cache --no-daemon --console=plain --info',
            description: 'Gradle Extra Options'
        )
        choice(
            description: 'Build Docker Image',
            choices: ['NO', 'YES'],
            name: 'OPT_BUILD_DOCKER'
        )
        choice(
            description: 'Publish Docker Image',
            choices: ['NO', 'YES'],
            name: 'OPT_PUBLISH_DOCKER'
        )
    }

    stages {
        stage('Build') {
            agent { label 'docker' }
            steps {
                sh "dev.sh"
            }
        }
    }
}
