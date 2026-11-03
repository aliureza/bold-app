#!/bin/sh
set -e
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
echo "Gradle is not installed. In GitHub Actions, use the workflow in .github/workflows/android.yml." >&2
exit 1
