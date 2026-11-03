# BOLD v2 implementation notes

- The provided archive contains only the `main/` Android source/resources and does **not** include Gradle wrapper/build files, so a clean Gradle build cannot be verified from this archive alone.
- The WebView UI is now split into `assets/index.html`, `assets/css/app.css`, and `assets/js/app.js`.
- Native reminder handling is in Java and survives WebView closure/reboot.
- Premium and Rewarded Ads are intentionally not faked. Real Google Play Billing requires a product ID, Play Console configuration, and (for secure entitlement verification) a backend/Play Developer API integration. Real AdMob Rewarded Ads likewise require an AdMob app/ad-unit configuration and the Google Mobile Ads SDK dependency.
- Local data is structured under one versioned `bold_v2` JSON object so a later repository/database sync layer can migrate it to Room/server storage.
- The 48-day challenge uses original concise exercises and does not reproduce book text.
