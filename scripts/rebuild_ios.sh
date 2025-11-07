#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

CONFIG="${CONFIG:-Debug}"
SDK_NAME="${SDK_NAME:-iphonesimulator}"
SCHEME="${SCHEME:-iosApp}"

if command -v /usr/libexec/java_home &>/dev/null; then
  export JAVA_HOME="$("/usr/libexec/java_home" -v 17)"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "==> Building Compose framework for $CONFIG / $SDK_NAME"
./gradlew \
  -PCONFIGURATION="$CONFIG" \
  -PSDK_NAME="$SDK_NAME" \
  :composeApp:packFinnAppForXcode \
  :iosApp:embedFinnAppFrameworkForXcode

DESTINATION=$([[ "$SDK_NAME" == iphoneos* ]] && echo "generic/platform=iOS" || echo "generic/platform=iOS Simulator")

echo "==> Triggering Xcode build ($SCHEME - $CONFIG)"
xcodebuild \
  -project iosApp/iosApp.xcodeproj \
  -scheme "$SCHEME" \
  -configuration "$CONFIG" \
  -destination "$DESTINATION" \
  CODE_SIGNING_ALLOWED=NO \
  build >/dev/null

echo "Done. You can now open iosApp/iosApp.xcodeproj"
