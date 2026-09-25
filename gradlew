#!/bin/sh
# Gradle start-up script (simplified bootstrap)
# Prefer regenerating with: gradle wrapper

DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
fi

# Fallback: use system gradle if available
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle wrapper jar missing. Run: gradle wrapper"
exit 1
