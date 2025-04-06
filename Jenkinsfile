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
            description: 'PG API',
            choices: ['NO', 'YES'],
            name: 'OPT_PG_API'
        )
        choice(
            description: 'Common Build',
            choices: ['NO', 'YES'],
            name: 'OPT_COMMON'
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
        stage('Common Build') {
            when {
                expression {
                    return params.OPT_COMMON == "YES"
                }
            }
            agent { label 'docker' }
            steps {
                sh "./dev.sh"
            }
        }
        stage('PG Api Build') {
            when {
                expression {
                    return params.OPT_PG_API == "YES"
                }
            }
            //agent { label 'docker' }
            steps {
                dir ("./java/pg-api") {
                    sh "./gradlew --no-daemon build"
                }
            }
        }
    }
}
