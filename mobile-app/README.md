# CMS Card — Mobile App

Mobile client for the deployed **cms-app** API.

## API (already configured)

```
http://46.224.146.158:8016
```

Set in `.env` / `.env.example` as `VITE_API_BASE_URL`.

**Important:** `capacitor.config.json` uses `"androidScheme": "http"` so the APK can call the HTTP API (avoids mixed-content / Failed to fetch).

## For Android developer — build APK

```bash
cd "Mobile App"
npm install
npm run build
npx cap sync android
```

Then either:

- `npx cap open android` → Build → Build APK(s)
- or: `cd android` → `gradlew.bat assembleDebug`

APK path:

`android/app/build/outputs/apk/debug/app-debug.apk`

Cleartext HTTP is enabled (`usesCleartextTraffic=true`).

## Zip tips

Exclude: `node_modules/`, `dist/`, `android/app/build/`, `android/.gradle/`
