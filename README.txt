BOLD Gradle Wrapper add-on

Copy these files/folders into the ROOT of your GitHub repository:

  gradlew
  gradlew.bat
  gradle/wrapper/gradle-wrapper.properties

The gradlew script will download the official Gradle 8.0 wrapper JAR on first run.
This matches the project's Android Gradle Plugin 8.0.0.

After copying, GitHub Actions can run:
  chmod +x gradlew
  ./gradlew assembleDebug
