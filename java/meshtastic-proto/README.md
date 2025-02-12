### 

How to publish:

```bash
export GH_TOKEN=$(cat ~/gh_token)
export GH_USERNAME=nefro85
./gradlew publishAllPublicationsToGitHubPackagesRepository --info

```

https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
   }
}
```

