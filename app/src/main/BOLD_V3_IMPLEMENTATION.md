# BOLD v3 — implemented changes

- Refreshed WebView UI to a warm off-white, pastel brand system.
- Logo palette now uses lavender, butter yellow, pastel sage green and soft coral; sage replaces the former blue in the logo system.
- Bottom navigation is black with inline SVG icons and improved active-state styling.
- Added animated splash screen using the BOLD mark.
- Added one-time onboarding name capture and persistent local storage of the user's name.
- Added time-aware greeting: صبح بخیر / ظهر بخیر / عصر بخیر / شب بخیر.
- Brand slogan is now: «داشته‌هایت را بولد کن.»
- Removed Yoga/Premium/Rewarded-Ad logic from the UI.
- Renamed the challenge area to «جعبه ابزار» and made «چالش‌ها» a toolbox entry.
- Added 4 challenges: 28-day gratitude path, 90-day 5-gratitudes + best-event path, 30-day question path, and 100-day 5-blessing reminder path.
- Challenge entries are stored per challenge and per day, with history visible inside the same challenge area.
- Reworked gratitude archive into an Instagram-like daily post feed, with per-day photo, caption-style gratitude list, date, and local like state.
- Daily gratitude photo is compressed before storage; images can be added while registering a gratitude or later from the daily feed.
- Fixed the daily 0/10 counter/progress to update immediately after a gratitude is saved.
- Rebuilt Story as a fuller editor: RTL text, right/center/left alignment, font size, line height, letter spacing, templates/backgrounds, logo toggle, custom TTF/OTF installation, text copy, sharing, and local PNG export.
- Story text now follows: «خدایا امروز بابت نعمت‌های ... تو را سپاسگزارم.»
- Added editable motivational messages with add/delete actions; messages can be selected for the daily notification.
- Updated the native Android reminder bridge so the selected motivational sentence is delivered by the notification.
- Feedback screen now presents an email address and opens the user's email client via mailto.
- Swipe navigation now moves in the RTL direction expected for the app and animates between pages.
- Updated the Android launcher icon and system navigation bar styling to match the new brand.
- Added the uploaded 29LTRavi-Regular TTF under assets/fonts and made it the primary app WebView font.

## Verification notes

- JavaScript syntax checked with Node.js (`node --check`).
- This upload contains no Gradle wrapper/build configuration, so a full Android Gradle build cannot be verified from the supplied archive alone.
- Replace `feedback@boldapp.ir` in `assets/js/app.js` with the final support email before release if a different address is used.
