#!/bin/sh

echo "Welcome to RCSSOFTWAREV2. To update and run, hit enter."
read -r

echo "--- Requirements ---"
echo ""
echo "1. JAVA"
echo "2. An Internet Connection (Wireless or Local; for Dial-Up, you just need broadband service installed.)"
echo "3. A default browser"
echo ""
echo "--------------------"
echo ""
echo "If your computer complies with these requirements, you may hit enter."
read -r

cd Data || { echo "Failed to navigate to 'Data' directory"; exit 1; }

java ZipFileUpdater || { echo "Failed to run ZipFileUpdater"; exit 1; }

cd ../Local || { echo "Failed to navigate to 'Local' directory"; exit 1; }

xdg-open "index.html" || { echo "Failed to open index.html"; exit 1; }
