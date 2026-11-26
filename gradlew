#!/bin/sh
# Minimal POSIX launcher for the Gradle Wrapper.
# The wrapper JAR reads gradle/wrapper/gradle-wrapper.properties and downloads
# the exact Gradle distribution pinned by this repository.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$CLASSPATH" ]; then
  echo "Gradle wrapper JAR not found: $CLASSPATH" >&2
  exit 1
fi
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
