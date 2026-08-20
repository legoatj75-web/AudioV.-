# AudioV revised build setup

This revision keeps the AudioV Kotlin + Jetpack Compose app architecture, UI, assets, and features intact.

Build-oriented changes:
- Removed generated build/signing artifacts from the archive.
- Added a Termux ARM64 AAPT2 override in `gradle.properties`.
- Added GitHub Actions workflow at `.github/workflows/build-apk.yml` for debug APK builds.
- No conversion from Jetpack Compose to Vybe's custom Android View architecture.
