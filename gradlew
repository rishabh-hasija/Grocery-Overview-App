#!/usr/bin/env sh
##############################################################################
# Gradle start up script for UN*X
##############################################################################
if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME=""
fi
DIRNAME="`dirname "$0"`"
APP_HOME="`cd "$DIRNAME" && pwd`"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$CLASSPATH" ]; then
  echo "ERROR: Gradle wrapper jar not found. Run 'gradle wrapper' or install Gradle." >&2
  exit 1
fi
exec java -jar "$CLASSPATH" "$@"
