# Releasing

1. Update the package version name and monotonically increasing version code.
2. Finish source, settings, docs, banner, screenshots, metadata, and test changes.
3. Run the full local test, coverage, style, privacy, documentation, and build commands.
4. Push the release candidate and wait for required GitHub Actions and configured provider checks to pass.
5. Perform a read-only review of the exact tested commit. If it needs edits, rerun the affected checks and CI.
6. Sign the APK using the established release key. Verify the APK signature, package, version, DreamService declaration, permissions, and signing certificate.
7. Generate `earthbound-orbit.apk.sha256` from the final signed APK. Publish both files with a SemVer tag and release notes describing features, fixes, platform evidence, commands, and upgrade path.
8. Download the published assets and verify the checksum against the published file. Keep the release key secure and backed up.

CI must pass before the final production sweep and release. Do not overwrite a published tag. Use a new patch version for fixes after release.
