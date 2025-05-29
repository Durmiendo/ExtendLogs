# MMP template

---
## What is it?
It's project template for mods based on my [mindustry mod plugin](https://github.com/nekit508/mindustry-mod-plugin).

---
## Before using.

### 1) All lines that commented with `reconfigure it` must be changed for your mod

Current files list with this marks:
- `settimgs.gradle`
- `build.gradle`
- `gradle.properties`

### 2) Configure your own local machine settings.

Create `settings/local.json` file.

All its settings:
- `build.useAndroid` - whether .dex file be built (If you do not know what it means, set this parameter `false`)
- `build.sdkRoot` - androidSDK root path (for example `D:/soft/android-sdk`)
- `copy` - list of paths where .jar file will be copied

Example of file:
```json
{
    "build": {
        "useAndroid": false
    },
    "copy": [
        "C:\\Users\\nekit508\\AppData\\Roaming\\Mindustry\\mods"
    ]
}
```

---
## How to build

```shell
./gradlew nmpCopyBuildRelease
```