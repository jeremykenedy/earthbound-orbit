# Continuous integration

GitHub Actions validates the Android build, host settings tests, installer tests, exact 100% line and branch coverage gates, Java formatting, documentation links, and app runtime privacy. CI builds unsigned review APKs. Release keys stay outside public CI. A release is signed outside public CI and includes a SHA-256 checksum file.

CI runs the project build, meaningful host tests, line and branch coverage gates, style checks, privacy checks, and documentation link checks. Optional provider integrations that require unconfigured credentials are not included.
