# DMZ: Generations

An addon for **[DragonMineZ](https://dragonminez.com/)** (Minecraft Forge 1.20.1).

- **Owner:** Yuseix300
- **Mod ID:** `dmzgenerations`
- **Minecraft:** 1.20.1 · **Forge:** 47.4.10 · **Java:** 17

## Requirements

This addon **requires** DragonMineZ and all of its dependencies (they are declared as
mandatory in `mods.toml`, so the game will refuse to load without them):

- DragonMineZ
- GeckoLib
- TerraBlender
- Curios

## Building

```bash
./gradlew build
```

The finished jar lands in `build/libs/`.

## Developing against the DragonMineZ API

The skeleton compiles on its own. To call DragonMineZ's own classes:

1. Build DragonMineZ (`./gradlew build` in its folder) and grab its dev/sources jar
   from `build/libs/`.
2. Copy it into this project's `libs/` folder and rename it to `dragonminez.jar`.
3. Uncomment the two `libs/dragonminez.jar` lines in `build.gradle`.
4. Refresh Gradle.

## Layout

```
src/main/java/com/yuseix300/dmzgenerations/DMZGenerations.java   # @Mod entry point
src/main/resources/META-INF/mods.toml                            # metadata + dependencies
src/main/resources/pack.mcmeta                                   # resource pack header
src/main/resources/assets/dmzgenerations/lang/en_us.json         # language keys
```
