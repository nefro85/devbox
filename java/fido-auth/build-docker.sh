#!/usr/bin/env bash

PROJECT_NAME=$(grep "rootProject.name" settings.gradle | sed -E "s/rootProject.name\s*=\s*'([^']+)'/\1/")

./gradlew shadowJar --console=plain

docker build -t s7i/${PROJECT_NAME} .

