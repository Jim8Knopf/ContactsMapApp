#!/bin/bash

set -e  # Exit on error

# Define paths
COLORS_FILE="app/src/main/res/values/colors.xml"
COLORS_NIGHT_FILE="app/src/main/res/values-night/colors.xml"

# Ensure values directories exist
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/values-night

echo "🌈 Fixing missing colors in colors.xml..."

# Define the missing colors in colors.xml
cat > "$COLORS_FILE" << 'EOF'
<resources>
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="purple_200">#BB86FC</color>
    <color name="purple_500">#6200EE</color>
    <color name="purple_700">#3700B3</color>
    <color name="teal_200">#03DAC5</color>
    <color name="teal_700">#018786</color>
</resources>
EOF
echo "✅ colors.xml updated."

echo "🌙 Fixing missing colors in colors-night.xml..."

# Define the missing colors in colors-night.xml (for dark mode)
cat > "$COLORS_NIGHT_FILE" << 'EOF'
<resources>
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="purple_200">#BB86FC</color>
    <color name="purple_500">#6200EE</color>
    <color name="purple_700">#3700B3</color>
    <color name="teal_200">#03DAC5</color>
    <color name="teal_700">#018786</color>
</resources>
EOF
echo "✅ colors-night.xml updated."

echo "🚀 Fix complete! Now sync Gradle in Android Studio and compile your app."
