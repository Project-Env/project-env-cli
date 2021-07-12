#!/bin/bash
set -e

KEY_CHAIN_PASSWORD="password"
KEY_CHAIN_NAME="project-env.keychain"

# create a new unlocked keychain containing the singing certificate
security create-keychain -p "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME"
security default-keychain -s "$KEY_CHAIN_NAME"
security unlock-keychain -p "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME"
echo "$APPLE_CERTIFICATE" | base64 --decode > certificate.p12
security import certificate.p12 -k "$KEY_CHAIN_NAME" -P "$APPLE_CERTIFICATE_PASSWORD" -T /usr/bin/codesign
security set-key-partition-list -S apple-tool:,apple:,codesign: -s -k "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME"

# sign the executable
codesign --force --deep --options=runtime -s "$APPLE_IDENTITY" "$EXECUTABLE_NAME" -v
security delete-keychain "$KEY_CHAIN_NAME"

# upload the signed executable to the notarization service
ditto -c -k "$EXECUTABLE_NAME" "$EXECUTABLE_NAME".zip
xcrun altool --notarize-app \
  --primary-bundle-id="$EXECUTABLE_ID" \
  --file="$EXECUTABLE_NAME".zip \
  --username="$APPLE_ID" \
  --password="$APPLE_ID_PASSWORD" \
  --asc-provider="$APPLE_ID_TEAM"
rm "$EXECUTABLE_NAME".zip

# update the package with the signed executable
rm "$EXECUTABLE_PACKAGE_NAME" && tar -czf "$EXECUTABLE_PACKAGE_NAME" "$EXECUTABLE_NAME"
