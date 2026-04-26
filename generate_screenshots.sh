#!/bin/bash

# Script to generate Play Store screenshots
echo "Starting automated screenshot generation..."

# Run the Gradle task
./gradlew :app:generatePlayStoreScreenshots

if [ $? -eq 0 ]; then
    echo "Screenshots generated successfully!"
    echo "Location: play-store-listing/"
else
    echo "Failed to generate screenshots. Check the logs above."
    exit 1
fi
