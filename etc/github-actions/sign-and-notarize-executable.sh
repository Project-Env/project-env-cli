#!/bin/bash
set -euo pipefail

KEY_CHAIN_PASSWORD="password"
KEY_CHAIN_NAME="project-env.keychain"

ORIGINAL_KEY_CHAIN=$(security default-keychain | tr -d ' "')

cleanup() {
  security default-keychain -s "$ORIGINAL_KEY_CHAIN" 2>/dev/null || true
  security delete-keychain "$KEY_CHAIN_NAME" 2>/dev/null || true
  rm -f certificate.p12 "$EXECUTABLE_NAME".zip
}
trap cleanup EXIT

# create a new unlocked keychain containing the signing certificate
security create-keychain -p "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME"
# a keychain locks itself after five minutes by default, which is shorter than a build
security set-keychain-settings -lut 3600 "$KEY_CHAIN_NAME"
security default-keychain -s "$KEY_CHAIN_NAME"
security unlock-keychain -p "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME"
echo "$APPLE_CERTIFICATE" | base64 --decode > certificate.p12
security import certificate.p12 -k "$KEY_CHAIN_NAME" -P "$APPLE_CERTIFICATE_PASSWORD" -T /usr/bin/codesign
security set-key-partition-list -S apple-tool:,apple:,codesign: -s -k "$KEY_CHAIN_PASSWORD" "$KEY_CHAIN_NAME" > /dev/null

# codesign reports an expired, revoked or untrusted certificate the same way as a missing
# one, namely 'no identity found'. Listing all identities next to the ones which are usable
# tells the two apart: an identity which appears above but not below is present but not
# usable, which points at the certificate itself rather than at this script.
echo "identities in the keychain:"
security find-identity -p codesigning "$KEY_CHAIN_NAME" || true
echo "identities usable for signing:"
security find-identity -v -p codesigning "$KEY_CHAIN_NAME" || true

# sign the executable. --deep is not used, because Apple recommends against it for signing
# and this is a single executable anyway. The timestamp is required for notarization.
codesign --force --options=runtime --timestamp -s "$APPLE_IDENTITY" "$EXECUTABLE_NAME" -v
codesign --verify --strict --verbose=2 "$EXECUTABLE_NAME"

# upload the signed executable to the notarization service. --wait makes a rejected
# notarization fail the build instead of passing unnoticed.
ditto -c -k "$EXECUTABLE_NAME" "$EXECUTABLE_NAME".zip
xcrun notarytool submit \
  --apple-id="$APPLE_ID" \
  --password="$APPLE_ID_PASSWORD" \
  --team-id="$APPLE_ID_TEAM" \
  --wait \
  "$EXECUTABLE_NAME".zip

# update the package with the signed executable
rm "$EXECUTABLE_PACKAGE_NAME" && tar -czf "$EXECUTABLE_PACKAGE_NAME" "$EXECUTABLE_NAME"
