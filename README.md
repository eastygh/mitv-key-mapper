# Xiaomi Mi Box 4 Vendor Key Remapping

**TVKeyMapper** is an Android TV application that replaces default apps launched by remote control buttons (e.g., Netflix, Live TV, etc.).

## Prerequisites
Before installing TVKeyMapper, uninstall default apps tied to your remote buttons.

### Uninstalling via Package Manager
Examples:
```shell
adb shell pm uninstall --user 0 com.netflix.ninja
```

## Installation
1. Install the APK on your Xiaomi Mi Box 4
2. Grant TVKeyMapper permission to read `logcat` (required for button press detection):
```shell
adb shell pm grant ru.easty.andoird.tvkeymapper android.permission.READ_LOGS
```

## Key Features
- Designed specifically for Android TV (Xiaomi Mi Box 4)
- Remaps vendor-locked remote buttons (Netflix/Live TV etc.)
- Requires `logcat` access for key event detection

