# DMZ: Generations

An addon for **[DragonMineZ](https://dragonminez.com/)** (Minecraft Forge 1.20.1).

- **Owner:** Yuseix300
- **Mod ID:** `dmzgenerations`
- **Minecraft:** 1.20.1 · **Forge:** 47.4.10 · **Java:** 17

## Info — How it works

DMZ: Generations adds an **age and generations** system on top of DragonMineZ: your character
is born young, ages as the days pass, and every life stage changes its strength, its looks and
its perks. Once you reach elderhood you can **rebirth** to pass your power on to a new, stronger
generation.

### 🎂 Aging

- You start at **age 1** (`defaultStartAge`).
- Every **7 Minecraft days** you grow **1 year** (`daysPerYear`). Only real world days count
  (sleeping to skip the night counts).
- The **Time Chamber** (Hyperbolic Time Chamber) accelerates aging — while inside you age
  `htcAgingMultiplier`× faster (default 30×).
- You **don't age while dead / in the Otherworld with a halo**.
- Every time your age ticks up you get a message that **you have grown**, an XP "ding" sound,
  and a burst of **totem particles** whenever you enter a new life stage.
- Age is stored per character and synced to the client (HUD, menu, model).

### 🧬 Life stages

Each stage applies a **multiplier to all your stats** (through DMZ's *bonus* system) and a
**model / hitbox scale**:

| Stage | Age | Stats | Model scale |
|---|---|---|---|
| Child (`child`) | 1 – 12 | ×0.75 | 0.5 |
| Teen (`teen`) | 13 – 24 | ×0.85 | 0.80 |
| Young adult (`young_adult`) | 25 – 39 | ×1.0 | **1.0** (original size) |
| Adult (`adult`) | 40 – 94 | ×1.2 | 1.0 |
| Elder (`elder`) | 95+ | ×1.0 | 0.90 (shrunken) |

The model **grows smoothly** between stages (interpolated by age), reaches its **original size
at young adult (age 25) and stops growing**. From adult to elder it shrinks slightly.

### ✨ Per-stage perks

- **Child** — +30% movement speed, −50% fall damage, and trains mastery **+10% faster**.
- **Teen** — +50% Ki and stamina regen, and trains mastery **+60% faster** (fastest learner).
- **Young adult** — +30% stamina regen, +30% mastery gain.
- **Adult** — combat peak: +15% critical hit chance.
- **Elder** — +50% Ki regen. Also a **mentor**: young players within an 8-block radius gain
  **+25% mastery** from their presence.

### 🫀 Old age

Being an elder is not free:

- **Ailments ("achaques")** — every so often your old heart aches and deals a bit of damage
  (default 2 hearts). The older you get, the **more frequent** they become (from every
  `elderAchaqueMaxIntervalSec` down to `elderAchaqueMinIntervalSec` at `elderAchaqueVeryOldAge`).
  By default they **never kill you** (they won't drop you below 2 HP) — set `elderAchaqueLethal`
  to change that. There is **no natural death**: the ailments are permanent, and the way out is
  to rebirth.
- **Gray hair** — your natural hair color fades toward gray/white with age, from
  `grayHairStartAge` (60) to fully gray at `grayHairFullAge` (120). Transformed hair (SSJ, etc.)
  keeps its own color.

### 🔥 Generations (the buff)

Each generation applies a **permanent multiplier to all your stats**:

| Generation | Multiplier |
|---|---|
| Gen 1 | ×1.0 |
| Gen 2 | ×1.2 |
| Gen 3 | ×1.4 |
| Gen 4 | ×1.6 |
| Gen 5 (max) | ×1.8 |

Each generation adds `+0.2` (`bonusPerGeneration`), up to a maximum of **5** (`maxGeneration`).

### ♻️ Rebirth

To move up a generation you must **rebirth**, by talking to **Enma** or **Dende** (a button
appears), or with the rebirth command. Requirements:

1. Be an **elder**.
2. Have **level ≥ 2000 × (target generation − 1)** (`levelPerGeneration`):
   Gen 2 → level 2000 · Gen 3 → 4000 · Gen 4 → 6000 · Gen 5 → 8000.

On rebirth, your progress is reset (like resetting your character) but you **gain a generation**
and go back to being a child with the new buff. By default you rebirth as the **same race**; if
`allowRaceChangeOnRebirth` is enabled, the **customization screen opens** so you can pick a new
race and appearance.

> ⚠️ Your generation **resets to Gen 1** if you use `/dmzstats reset` or reset your character
> with Dende. That's the trade-off for losing your progress.

### ⌨️ Commands

Require operator permission (level 2). `[targets]` is optional — leave it out to affect yourself.

| Command | What it does |
|---|---|
| `/dmzgen age get [targets]` | Shows age, stage, generation and multiplier. |
| `/dmzgen age set <years> [targets]` | Sets the exact age. |
| `/dmzgen age add <years> [targets]` | Adds (or subtracts) years. |
| `/dmzgen generation set <n> [targets]` | Sets the generation. |
| `/dmzgen rebirth [targets]` | Attempts a rebirth (enforces the requirements). |
| `/dmzgen reload` | Reloads `generations.json` and re-syncs everyone. |

Example: `/dmzgen age set 1` sets **your own** age to 1; `/dmzgen age set 1 Steve` sets Steve's.

### 🐉 "Restore Youth" wish

There is no special code for this — it's just a DragonMineZ **command wish**. Add a wish to your
dragonballs pack that runs the age command, and the dragon can make you young again:

```json
{
  "name": "Restore Youth",
  "description": "Return to the prime of your life.",
  "type": "command",
  "commands": ["dmzgen age set 25 %player%"]
}
```

Because it runs a command, it keeps your generation buff — only your age resets.

### 🖥️ Where you see it

- **HUD** — your age on screen.
- **Stats menu** — age and stage, and when you hover over your name.
- **Character creation** — the class tab has an **AGE:** selector with arrows; the preview model
  **changes size and shape to match the stage** you pick.

### ⚙️ Configuration

Almost everything is tunable in `config/dmzgenerations/generations.json` (JSON, like DMZ): days
per year, starting age, per-stage stat multipliers, perks, model scales, per-generation buff,
required level, race change on rebirth, Time Chamber aging speed (`htcAgingMultiplier`), elder
ailments (`elderAchaque*`), gray hair (`grayHair*`), growth feedback (`growthEffectsEnabled`),
etc. Run `/dmzgen reload` to apply changes live. Internal rendering details (how the hair texture
is fitted) are intentionally **not** exposed here.

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
