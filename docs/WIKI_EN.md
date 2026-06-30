# Wasteland Artifacts — Complete Wiki

> **Version:** v2.1.0 | **Platform:** Paper 1.21.11 | **Java:** 21

---

## Table of Contents

1. [Overview](#1-overview)
2. [Installation](#2-installation)
3. [Commands](#3-commands)
4. [Permissions](#4-permissions)
5. [Configuration Files](#5-configuration-files)
   - [config.yml](#51-configyml)
   - [custom_items.yml](#52-custom_itemsyml)
   - [artifacts/examples.yml](#53-artifactsexamplesyml)
   - [dungeons/default.yml](#54-dungeonsdefaultyml)
   - [mob_loot.yml](#55-mob_lootyml)
   - [altars.yml](#56-altarsyml)
   - [blueprint_workbench.yml](#57-blueprint_workbenchyml)
   - [rarities.yml](#58-raritiesyml)
   - [balance.yml](#59-balanceyml)
   - [lang/*.yml](#510-lang-files)
   - [features/*.yml](#511-featuresyml)
6. [Integrations](#6-integrations)
7. [CraftingProtection](#7-craftingprotection)
8. [Dungeon Loot System](#8-dungeon-loot-system)
9. [Custom Item Block Placement](#9-custom-item-block-placement)
10. [Database](#10-database)
11. [API](#11-api)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Overview

**Wasteland Artifacts** is a premium Minecraft plugin for Paper 1.21.11 that adds a complete artifact system to your server. It features custom artifacts with unique abilities, dungeon loot injection, 3D altar crafting, a mob drop system, boss system, artifact collection, active abilities, item upgrades, fishing loot, elite mobs, and a boss arena.

### Main Features

- **30+ Custom Artifacts** — weapons, armor, tools, accessories with unique abilities (fire aspect, lightning, life steal, explosions, summons, projectiles, AOE, etc.)
- **86 Custom Ingredients** — crafting components organized into categories (Elements, Creature Parts, Ancient Artifacts, Alchemy, Wasteland, Cosmos, Materials, Mythic, Special, Added)
- **3 Altar Tiers** — 3D multi-block structures for crafting artifacts (Basic, Advanced, Legendary)
- **Blueprint System** — craft blueprints in a vanilla workbench, use them on altars
- **Dungeon Loot Injection** — 20+ vanilla structure types with custom loot tables
- **Boss System** — custom bosses that spawn when chests are opened in dungeons
- **Mob Loot** — custom drops from 30+ mob types
- **Artifact Bag** — portable artifact storage with automatic effect application
- **Resource Pack** — auto-generated resource pack with custom models and textures
- **Multi-Language** — 5 built-in language files (EN, RU, DE, FR, ZH)
- **Feature System** — 12 toggleable modules, each enabled/disabled in config.yml
- **Artifact Collection** — `/artifact collection` GUI with database tracking
- **Artifact Sets** — set bonuses for wearing matching artifacts (features/sets.yml)
- **Active Abilities** — 6 types (PROJECTILE, TELEPORT, DASH, SHIELD, HEAL, AOE, COMMAND)
- **Artifact Upgrades** — combine identical artifacts to increase levels
- **Fishing Loot** — custom fishing drops (features/fishing_loot.yml)
- **Elite Mobs** — rare mob variants with multipliers and custom drops
- **Artifact XP** — XP per kill, level-up, damage scaling
- **Boss Arena** — wave-based arena with rewards (features/arena_config.yml)
- **AdminItemsGUI** — `/waadmin gui` with categorized tabs
- **Integrations**: ItemsAdder, Nexo, Oraxen, MythicMobs, PlaceholderAPI — all through reflection
- **Developer API** — public API for registering custom artifacts, components, and triggers

---

## 2. Installation

### Requirements

- Java 21 or higher
- Paper 1.21.1 or higher (tested on 1.21.11)

### Optional Dependencies

- **WorldEdit** or **FastAsyncWorldEdit** — for schematic pasting
- **ItemsAdder**, **Nexo** or **Oraxen** — for using custom items in recipes and loot
- **MythicMobs** — for elite mob integration
- **PlaceholderAPI** — for placeholders

### Installation Steps

1. Download the latest `WastelandArtifacts.jar` from the releases page
2. Place the JAR in your server's `plugins/` folder
3. Restart your server
4. All configuration files will be auto-generated on first start:
   - `plugins/WastelandArtifacts/config.yml`
   - `plugins/WastelandArtifacts/custom_items.yml`
   - `plugins/WastelandArtifacts/altars.yml`
   - `plugins/WastelandArtifacts/blueprint_workbench.yml`
   - `plugins/WastelandArtifacts/rarities.yml`
   - `plugins/WastelandArtifacts/balance.yml`
   - `plugins/WastelandArtifacts/mob_loot.yml`
   - `plugins/WastelandArtifacts/artifacts/examples.yml`
   - `plugins/WastelandArtifacts/dungeons/default.yml`
   - `plugins/WastelandArtifacts/features/collection.yml`
   - `plugins/WastelandArtifacts/features/sets.yml`
   - `plugins/WastelandArtifacts/features/abilities.yml`
   - `plugins/WastelandArtifacts/features/upgrades.yml`
   - `plugins/WastelandArtifacts/features/fishing_loot.yml`
   - `plugins/WastelandArtifacts/features/elites.yml`
   - `plugins/WastelandArtifacts/features/xp.yml`
   - `plugins/WastelandArtifacts/features/arena.yml`
   - `plugins/WastelandArtifacts/lang/en_US.yml`
   - `plugins/WastelandArtifacts/lang/ru_RU.yml`
   - `plugins/WastelandArtifacts/lang/de_DE.yml`
   - `plugins/WastelandArtifacts/lang/fr_FR.yml`
   - `plugins/WastelandArtifacts/lang/zh_CN.yml`

### First Start Behavior

- Scans all worlds for dungeons (if `scanOnStartup: true`)
- Registers all blueprint crafting recipes
- Starts the HTTP resource pack server (port 8192 by default)
- Generates custom model data JSON files for all artifacts
- Initializes the database (SQLite/MySQL)
- Loads feature modules according to config.yml

---

## 3. Commands

### 3.1 `/artifact` — Artifact Management

**Aliases:** `/art`, `/wa`

| Subcommand | Permission | Args | Description |
|---|---|---|---|
| `give` | `wastelandartifacts.admin.blueprint` | `<id> [player] [amount]` | Give an artifact to a player |
| `list` | `wastelandartifacts.player.artifact` | `[page]` | List all registered artifacts |
| `info` | `wastelandartifacts.player.artifact` | `<id>` | Show detailed info about an artifact |
| `reload` | `wastelandartifacts.admin` | — | Reload all configuration files |
| `create` | `wastelandartifacts.admin` | — | Open the in-game artifact editor GUI |
| `edit` | `wastelandartifacts.admin` | `<id>` | Edit an existing artifact in the GUI |
| `collection` | `wastelandartifacts.player.collection` | — | Show artifact collection progress |

**Examples:**
```
/artifact give fire_sword PlayerName 1
/artifact list 1
/artifact info necronomicon
/artifact reload
/artifact create
/artifact edit fire_sword
/artifact collection
```

### 3.2 `/waadmin` — Admin Commands

**Aliases:** `/waa`

| Subcommand | Permission | Args | Description |
|---|---|---|---|
| `gui` | `wastelandartifacts.admin.gui` | — | Open admin items GUI (artifacts, custom items, blueprints) |
| `rp build` | `wastelandartifacts.admin.rp` | — | Build the resource pack ZIP |
| `rp send` | `wastelandartifacts.admin.rp` | — | Send resource pack to all online players |
| `blueprint` | `wastelandartifacts.admin.blueprint` | `<recipe_id>` | Give a blueprint item |
| `customitem` | `wastelandartifacts.admin.customitem` | `<id> [amount]` | Give a custom item ingredient |
| `debug` | `wastelandartifacts.admin.debug` | — | Show debug info (artifact count, components, dungeons, schematics, resource pack status) |

**Examples:**
```
/waadmin gui
/waadmin rp build
/waadmin rp send
/waadmin blueprint basic_altar_craft_fire_sword
/waadmin customitem fire_core 16
/waadmin debug
```

### 3.3 `/artifact` — Artifact Management (continued)

The `/artifact collection` subcommand opens the artifact collection GUI, showing which artifacts the player has found and which are still to be discovered.

### 3.4 `/altar` — Altar System

**Aliases:** `/alt`

| Subcommand | Permission | Args | Description |
|---|---|---|---|
| `list` | `wastelandartifacts.player.altar` | — | List all altar tiers |
| `info` | `wastelandartifacts.player.altar` | `<tier>` | Show altar info and recipes |
| `preview` | `wastelandartifacts.player.altar` | `<tier> [variant]` | Show hologram preview of altar structure |
| `preview stop` | `wastelandartifacts.player.altar` | — | Stop the hologram preview |
| `build` | `wastelandartifacts.admin.altar` | `<tier> [variant]` | Auto-build the altar (consumes blocks from inventory) |
| `schematic save` | `wastelandartifacts.admin.altar` | `<name>` | Save current altar as a schematic |
| `schematic paste` | `wastelandartifacts.admin.altar` | `<name>` | Paste an altar schematic |
| `schematic list` | `wastelandartifacts.admin.altar` | — | List saved altar schematics |

**Examples:**
```
/altar list
/altar info basic_altar
/altar preview basic_altar
/altar preview stop
/altar build basic_altar
/altar schematic save my_altar
/altar schematic paste my_altar
```

### 3.5 `/bag` — Artifact Bag

**Aliases:** `/artifacts`, `/artbag`

**Permission:** `wastelandartifacts.player.bag`

**Usage:** `/bag`

Opens the Artifact Bag GUI. Requires the **Wasteland Bag** artifact in your inventory. Only artifacts can be placed inside. Effects from bagged artifacts with `POTION_EFFECT_ON_EQUIP` components are automatically applied to the player.

### 3.6 `/dungeon` — Dungeon Management

**Aliases:** `/dg`

**Permission:** `wastelandartifacts.admin`

| Subcommand | Description |
|---|---|
| `scan [world]` | Scan world(s) for structures and map them to dungeon configs |
| `paste <schematic>` | Paste a schematic at the player's location |
| `loot` | Open dungeon loot GUI (view configured loot for each dungeon) |
| `info` | Show dungeon statistics (config count, schematic count) |

**Examples:**
```
/dungeon scan world
/dungeon paste my_dungeon
/dungeon loot
/dungeon info
```

### 3.7 `/item` — Item Encode/Decode

**Permission:** Requires base permission

| Subcommand | Description |
|---|---|
| `encode` | Get the Mojang Base64 NBT string of the item in your hand |
| `decode <base64>` | Create an item from a Base64 NBT string |

**Examples:**
```
/item encode
/item decode H4sI...
```

### 3.8 `/arena` — Boss Arena

**Aliases:** `/bossarena`

**Permission:** `wastelandartifacts.player.arena`

| Subcommand | Description |
|---|---|
| *(no arguments)* | Open the boss arena GUI |
| `start` | Start a wave on the arena |

**Examples:**
```
/arena
/arena start
```

---

## 4. Permissions

| Permission Node | Default | Parent | Description |
|---|---|---|---|
| `wastelandartifacts.*` | op | `admin` + `player` | All permissions |
| `wastelandartifacts.admin` | op | `admin.altar`, `admin.blueprint`, `admin.customitem`, `admin.debug`, `admin.rp`, `admin.gui` | All admin commands |
| `wastelandartifacts.player` | true | `player.altar`, `player.bag`, `player.artifact`, `player.blueprint`, `player.collection`, `player.arena` | All player commands |
| `wastelandartifacts.admin.altar` | op | — | Altar management (build, schematic) |
| `wastelandartifacts.admin.blueprint` | op | — | Give blueprints |
| `wastelandartifacts.admin.customitem` | op | — | Give custom items |
| `wastelandartifacts.admin.debug` | op | — | Debug commands |
| `wastelandartifacts.admin.rp` | op | — | Resource pack management |
| `wastelandartifacts.admin.gui` | op | — | Admin items GUI |
| `wastelandartifacts.player.altar` | true | — | View and preview altars |
| `wastelandartifacts.player.bag` | true | — | Use the artifact bag |
| `wastelandartifacts.player.artifact` | true | — | List and view artifact info |
| `wastelandartifacts.player.blueprint` | true | — | Use blueprints on altars |
| `wastelandartifacts.player.collection` | true | — | View artifact collection |
| `wastelandartifacts.player.arena` | true | — | Access the boss arena |

---

## 5. Configuration Files

### 5.1 config.yml

The main configuration file located at `plugins/WastelandArtifacts/config.yml`.

```yaml
# ─── Wasteland Artifacts — Main Config ───

features:
  placeholderAPI: true
  mythicmobs: true
  nexo: true
  oraxen: true
  collection: true
  artifactSets: true
  activeAbilities: true
  upgrades: true
  fishing: true
  customMobs: true
  artifactXP: true
  bossArena: true

resource-pack:
  mode: AUTO
  autoHost: true
  hostPort: 8192
  force: true
  prompt: "<gold>This server uses a custom resource pack!"
  hash: ""

dungeons:
  enabled: true
  scanOnStartup: true
  lootInjection: true
  specialChests: true
  bossSpawners: true

crafting:
  enabled: true
  altarEnabled: true
  keepIngredients: false

artifacts:
  defaultAmount: 1
  dropOnDeath: false
  soulbind: false
  allowInAnvil: false

database:
  enabled: true
  type: SQLITE
  host: localhost
  port: 3306
  database: wasteland_artifacts
  user: root
  password: ""
  poolSize: 10

gui:
  rows: 6
  title: "<dark_gray>Wasteland Artifacts"
  fill-glass: true

lang:
  locale: en_US
```

#### Features Section

Controls plugin module toggles. Allows flexible feature customization per server:

| Setting | Type | Default | Description |
|---|---|---|---|
| `placeholderAPI` | Boolean | `true` | Enable PlaceholderAPI integration |
| `mythicmobs` | Boolean | `true` | Enable MythicMobs integration |
| `nexo` | Boolean | `true` | Enable Nexo integration |
| `oraxen` | Boolean | `true` | Enable Oraxen integration |
| `collection` | Boolean | `true` | Enable artifact collection system |
| `artifactSets` | Boolean | `true` | Enable artifact set bonuses |
| `activeAbilities` | Boolean | `true` | Enable active abilities |
| `upgrades` | Boolean | `true` | Enable artifact upgrades |
| `fishing` | Boolean | `true` | Enable custom fishing loot |
| `customMobs` | Boolean | `true` | Enable elite mobs |
| `artifactXP` | Boolean | `true` | Enable artifact XP |
| `bossArena` | Boolean | `true` | Enable boss arena |

#### Resource Pack Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `mode` | String | `AUTO` | Resource pack mode (AUTO or MANUAL) |
| `autoHost` | Boolean | `true` | Start built-in HTTP server for hosting |
| `hostPort` | Integer | `8192` | Port for the HTTP server |
| `force` | Boolean | `true` | Force players to accept the resource pack |
| `prompt` | String | `"..."` | Prompt message shown to players |
| `hash` | String | `""` | SHA-1 hash (auto-computed if empty) |

#### Dungeons Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `enabled` | Boolean | `true` | Enable dungeon loot system |
| `scanOnStartup` | Boolean | `true` | Scan all worlds for structures on startup |
| `lootInjection` | Boolean | `true` | Inject custom loot into structure chests |
| `specialChests` | Boolean | `true` | Enable special chest handling |
| `bossSpawners` | Boolean | `true` | Enable boss spawning on chest open |

#### Crafting Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `enabled` | Boolean | `true` | Enable crafting system |
| `altarEnabled` | Boolean | `true` | Enable altar crafting |
| `keepIngredients` | Boolean | `false` | Keep ingredients after crafting (creative mode) |

#### Artifacts Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `defaultAmount` | Integer | `1` | Default amount when giving artifacts |
| `dropOnDeath` | Boolean | `false` | Drop artifacts on player death |
| `soulbind` | Boolean | `false` | Soulbind artifacts to players |
| `allowInAnvil` | Boolean | `false` | Allow artifacts in anvils |

#### Database Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `enabled` | Boolean | `true` | Enable database storage |
| `type` | String | `SQLITE` | Database type (SQLITE or MYSQL) |
| `host` | String | `localhost` | MySQL host |
| `port` | Integer | `3306` | MySQL port |
| `database` | String | `wasteland_artifacts` | Database name |
| `user` | String | `root` | MySQL user |
| `password` | String | `""` | MySQL password |
| `poolSize` | Integer | `10` | HikariCP connection pool size |

#### GUI Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `rows` | Integer | `6` | Number of rows in the artifact GUI (max 6) |
| `title` | String | `"..."` | GUI title (MiniMessage format) |
| `fill-glass` | Boolean | `true` | Fill empty slots with glass panes |

#### Lang Section

| Setting | Type | Default | Description |
|---|---|---|---|
| `locale` | String | `en_US` | Default locale (`en_US`, `ru_RU`, `de_DE`, `fr_FR`, `zh_CN`) |

---

### 5.2 custom_items.yml

Located at `plugins/WastelandArtifacts/custom_items.yml`. Contains **86 custom items** used as crafting ingredients for artifacts. Each item has a unique `customModelData` value (6001–6086).

#### Format

```yaml
items:
  item_id:
    material: MATERIAL
    name: "<color>Display Name"
    lore:
      - "<gray>Lore line 1"
      - "<dark_gray>Lore line 2"
    customModelData: 6001
    rarity: UNCOMMON
```

| Field | Type | Description |
|---|---|---|
| `item_id` | String | Unique identifier used in recipes and loot tables |
| `material` | Material | Vanilla Minecraft material |
| `name` | String | Display name (MiniMessage format) |
| `lore` | List | Lore lines (MiniMessage format) |
| `customModelData` | Integer | Custom model data value |
| `rarity` | String | Rarity from rarities.yml |

#### Full Item List

**Elements (8):**
- `fire_core` (BLAZE_POWDER, CMD 6001, UNCOMMON)
- `shadow_essence` (INK_SAC, CMD 6002, RARE)
- `frozen_shard` (ICE, CMD 6003, UNCOMMON)
- `storm_crystal` (AMETHYST_SHARD, CMD 6004, EPIC)
- `earth_core` (COBBLESTONE, CMD 6016, UNCOMMON)
- `wind_essence` (FEATHER, CMD 6017, UNCOMMON)
- `water_pearl` (PRISMARINE_SHARD, CMD 6018, RARE)
- `light_fragment` (GLOWSTONE_DUST, CMD 6019, RARE)

**Creature Parts (8):**
- `warden_heart` (SCULK, CMD 6005, LEGENDARY)
- `ancient_bone` (BONE, CMD 6007, UNCOMMON)
- `dragon_scales` (PRISMARINE_CRYSTALS, CMD 6009, LEGENDARY)
- `wither_skull` (WITHER_SKELETON_SKULL, CMD 6013, LEGENDARY)
- `eye_of_abyss` (ENDER_EYE, CMD 6020, EPIC)
- `shadow_claw` (BLACK_DYE, CMD 6021, RARE)
- `phantom_wing` (PHANTOM_MEMBRANE, CMD 6022, RARE)
- `horn_of_dread` (BONE, CMD 6023, EPIC)

**Ancient Artifacts (9):**
- `totem_fragment` (TOTEM_OF_UNDYING, CMD 6014, EPIC)
- `ancient_coin` (GOLD_NUGGET, CMD 6024, UNCOMMON)
- `runic_tablet` (STONE, CMD 6025, RARE)
- `elder_shard` (PRISMARINE_SHARD, CMD 6026, EPIC)
- `forgotten_key` (TRIPWIRE_HOOK, CMD 6027, RARE)
- `void_seal` (OBSIDIAN, CMD 6028, EPIC)
- `ethereal_orb` (GLOWSTONE, CMD 6029, LEGENDARY)
- `ancient_crown` (GOLD_INGOT, CMD 6030, LEGENDARY)
- `ring_of_pact` (IRON_NUGGET, CMD 6031, RARE)

**Alchemy (9):**
- `soul_fragment` (GHAST_TEAR, CMD 6008, RARE)
- `arcane_essence` (EXPERIENCE_BOTTLE, CMD 6032, RARE)
- `spectral_dust` (GUNPOWDER, CMD 6033, UNCOMMON)
- `chaos_crystal` (AMETHYST_SHARD, CMD 6034, EPIC)
- `alchemic_potion` (POTION, CMD 6035, RARE)
- `primal_catalyst` (BLAZE_ROD, CMD 6036, EPIC)
- `life_elixir` (HONEY_BOTTLE, CMD 6037, LEGENDARY)
- `purified_salt` (SUGAR, CMD 6038, UNCOMMON)
- `quicksilver` (IRON_NUGGET, CMD 6039, RARE)

**Wasteland (8):**
- `wasteland_sand` (SAND, CMD 6040, COMMON)
- `withered_bone` (BONE, CMD 6041, COMMON)
- `thorn_vine` (VINE, CMD 6042, UNCOMMON)
- `rusted_ingot` (IRON_INGOT, CMD 6043, UNCOMMON)
- `ash_pile` (GUNPOWDER, CMD 6044, COMMON)
- `toxic_slime` (SLIME_BALL, CMD 6045, UNCOMMON)
- `fungal_spore` (RED_MUSHROOM, CMD 6046, RARE)
- `poison_gland` (SPIDER_EYE, CMD 6047, RARE)

**Cosmos (9):**
- `void_crystal` (ECHO_SHARD, CMD 6006, EPIC)
- `ender_gem` (ENDER_EYE, CMD 6015, RARE)
- `celestial_star` (NETHER_STAR, CMD 6048, LEGENDARY)
- `moon_shard` (QUARTZ, CMD 6049, RARE)
- `sun_fragment` (GLOWSTONE, CMD 6050, EPIC)
- `void_essence` (BLACK_DYE, CMD 6051, EPIC)
- `galaxy_dust` (GLOW_BERRIES, CMD 6052, RARE)
- `nebula_crystal` (AMETHYST_SHARD, CMD 6053, EPIC)
- `meteor_iron` (IRON_INGOT, CMD 6054, RARE)

**Materials (9):**
- `corrupted_ingot` (NETHERITE_SCRAP, CMD 6010, EPIC)
- `phantom_weave` (PHANTOM_MEMBRANE, CMD 6011, RARE)
- `enchanted_cloth` (WHITE_WOOL, CMD 6055, UNCOMMON)
- `ancient_leather` (LEATHER, CMD 6056, UNCOMMON)
- `spectral_thread` (STRING, CMD 6057, RARE)
- `reinforced_plate` (IRON_INGOT, CMD 6058, UNCOMMON)
- `clockwork_gear` (IRON_NUGGET, CMD 6059, RARE)
- `energy_spring` (COPPER_INGOT, CMD 6060, UNCOMMON)
- `magnetic_core` (IRON_BLOCK, CMD 6061, RARE)

**Mythic (8):**
- `magma_core` (MAGMA_BLOCK, CMD 6012, UNCOMMON)
- `phoenix_feather` (FEATHER, CMD 6062, LEGENDARY)
- `unicorn_horn` (BONE, CMD 6063, LEGENDARY)
- `titan_heart` (NETHERITE_SCRAP, CMD 6064, LEGENDARY)
- `siren_tear` (HEART_OF_THE_SEA, CMD 6065, EPIC)
- `chimera_claw` (FLINT, CMD 6066, EPIC)
- `hydra_tooth` (BONE, CMD 6067, EPIC)
- `kraken_tentacle` (PRISMARINE_CRYSTALS, CMD 6068, LEGENDARY)

**Special (8):**
- `poseidon_flame` (BLAZE_POWDER, CMD 6069, LEGENDARY)
- `heart_of_poseidon` (HEART_OF_THE_SEA, CMD 6070, VOID)
- `ancient_scrap` (NETHERITE_SCRAP, CMD 6071, RARE)
- `golden_feather` (FEATHER, CMD 6072, RARE)
- `spider_fang` (BONE, CMD 6073, UNCOMMON)
- `echo_shard` (ECHO_SHARD, CMD 6074, RARE)
- `prismarine_core` (PRISMARINE_CRYSTALS, CMD 6075, UNCOMMON)
- `obsidian_shard` (OBSIDIAN, CMD 6076, UNCOMMON)

**Added (v2.0.0) (10):**
- `crystal_heart` (DIAMOND, CMD 6077, LEGENDARY)
- `demon_blood` (RED_DYE, CMD 6078, EPIC)
- `star_dust` (GLOW_BERRIES, CMD 6079, RARE)
- `frozen_heart` (ICE, CMD 6080, EPIC)
- `thunder_feather` (FEATHER, CMD 6081, RARE)
- `shadow_fragment` (BLACK_DYE, CMD 6082, EPIC)
- `phoenix_ashes` (GUNPOWDER, CMD 6083, LEGENDARY)
- `titan_armor_plate` (NETHERITE_SCRAP, CMD 6084, LEGENDARY)
- `void_tentacle` (PRISMARINE_CRYSTALS, CMD 6085, EPIC)
- `angel_feather` (FEATHER, CMD 6086, LEGENDARY)

> **Note:** You can add your own custom items by following the same format. Items are referenced in recipes and loot tables using `custom:item_id` prefix (e.g., `custom:fire_core`).

---

### 5.3 artifacts/examples.yml

Located at `plugins/WastelandArtifacts/artifacts/examples.yml`. Contains **30+ fully working example artifacts**. You can add your own artifact files in the `artifacts/` directory, and they will be auto-loaded.

#### Artifact YAML Format

```yaml
artifacts:
  - id: "fire_sword"
    displayName: "<red>Fire Sword"
    lore:
      - "<gray>Forged in the depths of the Wasteland"
      - "<dark_red>♨ Sets enemies on fire"
    baseItem: DIAMOND_SWORD
    customModelData: 1001
    rarity: RARE
    unbreakable: true
    maxStackSize: 1
    skinTexture: "base64_encoded_texture..."
    components:
      - type: DAMAGE
        damage: 12.0
      - type: FIRE_ASPECT
        level: 2
      - type: LIGHTNING
        chance: 0.15
      - type: PARTICLE_ON_HIT
        particle: FLAME
        count: 15
      - type: SOUND_ON_HIT
        sound: ENTITY_BLAZE_HURT
      - type: COOLDOWN
        seconds: 3
    recipe:
      type: shaped
      pattern:
        - " B "
        - "BSB"
        - " B "
      ingredients:
        B: BLAZE_ROD
        S: NETHERITE_SCRAP
```

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | String | Yes | Unique artifact identifier |
| `displayName` | String | Yes | Display name (MiniMessage) |
| `lore` | List | No | Lore lines (MiniMessage) |
| `baseItem` | Material | Yes | Vanilla material base |
| `customModelData` | Float | No | Custom model data value |
| `rarity` | String | Yes | Rarity from rarities.yml |
| `unbreakable` | Boolean | No | Makes the item unbreakable |
| `maxStackSize` | Integer | No | Max stack size (default: material default) |
| `skinTexture` | String | No | Base64 skin texture for PLAYER_HEAD items |
| `components` | List | Yes | List of artifact components |
| `recipe` | Object | No | Crafting recipe (shaped or shapeless) |

#### Ingredient Types in Recipes

Recipes support 4 ingredient formats:

```yaml
# 1) Simple material by name:
A: DIAMOND

# 2) Reference to another artifact:
B: "artifact:fire_sword"

# 3) Reference to a custom item from custom_items.yml:
C: "custom:fire_core"

# 4) Extended format with name/lore/CMD matching (supports cross-plugin items):
D:
  type: DIAMOND_SWORD
  name: "<gold>Ancient Blade"
  lore:
    - "<gray>An ancient weapon"
  customModelData: 5010
```

#### Components System

Each component adds a specific ability to the artifact. Below is the complete list of **17 component types**:

##### DAMAGE
Adds attack damage modifier.
```yaml
- type: DAMAGE
  damage: 12.0
```

##### FIRE_ASPECT
Adds Fire Aspect enchantment.
```yaml
- type: FIRE_ASPECT
  level: 2
```

##### ATTRIBUTE
Adds a generic attribute modifier.
```yaml
- type: ATTRIBUTE
  attribute: GENERIC_ARMOR
  amount: 3.0
  operation: ADD_NUMBER        # ADD_NUMBER | MULTIPLY_SCALAR_1 | ADD_SCALAR
```

**Supported attributes:** GENERIC_ARMOR, GENERIC_ARMOR_TOUGHNESS, GENERIC_ATTACK_DAMAGE, GENERIC_MAX_HEALTH, GENERIC_MOVEMENT_SPEED, GENERIC_KNOCKBACK_RESISTANCE, GENERIC_SAFE_FALL_DISTANCE, GENERIC_JUMP_STRENGTH

**Operations:** `ADD_NUMBER` (flat addition), `MULTIPLY_SCALAR_1` (percentage multiplier), `ADD_SCALAR`

##### POTION_EFFECT_ON_EQUIP
Applies a potion effect while the artifact is equipped (in armor slot, offhand, or in bag).
```yaml
- type: POTION_EFFECT_ON_EQUIP
  effect: RESISTANCE
  amplifier: 1
  ambient: true                  # Shows reduced particle effect
```

##### POTION_EFFECT_ON_HIT
Applies a potion effect to the target on hit.
```yaml
- type: POTION_EFFECT_ON_HIT
  effect: POISON
  duration: 100                  # In ticks (5 seconds = 100)
  amplifier: 1
```

##### PARTICLE_ON_HIT
Spawns particles when the target is hit.
```yaml
- type: PARTICLE_ON_HIT
  particle: FLAME
  count: 15
```

##### PARTICLE_AMBIENT
Spawns ambient particles while the artifact is equipped.
```yaml
- type: PARTICLE_AMBIENT
  particle: SOUL
  count: 5
```

##### SOUND_ON_HIT
Plays a sound when the entity is hit.
```yaml
- type: SOUND_ON_HIT
  sound: ENTITY_BLAZE_HURT
```

##### SOUND_ON_USE
Plays a sound when the artifact is used (right-click).
```yaml
- type: SOUND_ON_USE
  sound: ENTITY_ILLUSIONER_CAST_SPELL
  volume: 1.0
  pitch: 0.8
```

##### COOLDOWN
Adds a cooldown after using the artifact.
```yaml
- type: COOLDOWN
  seconds: 3
```

##### LIFE_STEAL
Steals health from the target on hit.
```yaml
- type: LIFE_STEAL
  percentage: 0.15               # 15% of damage dealt
```

##### LIGHTNING
Strikes lightning on the target on hit.
```yaml
- type: LIGHTNING
  chance: 0.15                   # 15% chance
  safe: true                     # Doesn't damage the attacker
```

##### EXPLOSION
Creates an explosion on use.
```yaml
- type: EXPLOSION
  power: 3.0
  safe: true                     # Doesn't damage the player
```

##### SUMMON
Summons mobs on use.
```yaml
- type: SUMMON
  entityType: SKELETON
  amount: 3
  duration: 300                  # Ticks (15 seconds)
  withEquipment: true            # Spawns with equipment
```

##### PROJECTILE
Shoots a projectile on use.
```yaml
- type: PROJECTILE
  projectileType: FIREBALL       # FIREBALL, WITHER_SKULL, etc.
  speed: 2.0
```

##### AOE
Creates an area-of-effect around the player.
```yaml
- type: AOE
  radius: 8.0
  damage: 10.0                   # Use negative for healing
  effect: SLOWNESS               # Potion effect to apply (optional)
  amplifier: 2                   # Effect amplifier (optional)
  duration: 100                  # Effect duration in ticks (optional)
```

##### CHARGE
Adds limited charges to the artifact.
```yaml
- type: CHARGE
  maxCharges: 5
  consumeOnUse: true             # Consume one charge per use
  destroyWhenEmpty: false        # Destroy the item when out of charges
```

##### COMMAND
Executes a command on use.
```yaml
- type: COMMAND
  command: "bag"                 # Command to execute (without /)
  asPlayer: true                 # Execute as the player (vs console)
```

#### Trigger Types

The trigger system allows components to respond to specific events. Artifact components can be configured to fire on specific triggers:

| TriggerType | Description |
|---|---|
| `ON_ATTACK` | When the player attacks an entity |
| `ON_HIT` | When the player is hit by an entity |
| `ON_USE` | When the player right-clicks with the artifact |
| `ON_EQUIP` | When the artifact is equipped |
| `ON_UNEQUIP` | When the artifact is unequipped |
| `ON_PICKUP` | When the artifact is picked up |
| `ON_SNEAK` | When the player sneaks |
| `ON_SWING` | When the player swings their hand |
| `ON_PROJECTILE_HIT` | When a projectile hits |
| `ON_KILL` | When the player kills an entity |
| `ON_DEATH` | When the player dies |
| `ON_SPRINT` | When the player sprints |
| `ON_TIMER` | Periodic timer-based trigger |
| `ON_DAMAGE_TAKEN` | When the player takes damage |

#### Recipe Format

##### Shaped Recipe
```yaml
recipe:
  type: shaped
  pattern:
    - "ABC"
    - "DEF"
    - "GHI"
  ingredients:
    A: DIAMOND
    B: "artifact:fire_sword"
    C: "custom:fire_core"
```

##### Shapeless Recipe
```yaml
recipe:
  type: shapeless
  ingredients:
    - DIAMOND
    - "custom:fire_core"
    - "artifact:fire_sword"
```

---

### 5.4 dungeons/default.yml

Located at `plugins/WastelandArtifacts/dungeons/default.yml`. Contains configurations for **20 structure types** — both vanilla structures and custom schematics.

#### Structure Types Configured

| Structure ID | Vanilla Structure | Loot Mode |
|---|---|---|
| `stronghold` | Stronghold | INJECT |
| `fortress` | Nether Fortress | INJECT |
| `pillager_outpost` | Pillager Outpost | INJECT |
| `ruined_portal` | Ruined Portal | INJECT |
| `buried_treasure` | Buried Treasure | INJECT |
| `mineshaft` | Mineshaft | INJECT |
| `ocean_ruins` | Ocean Ruins | INJECT |
| `trail_ruins` | Trail Ruins | INJECT |
| `trial_chambers` | Trial Chambers | INJECT |
| `ancient_city` | Ancient City | REPLACE |
| `bastion` | Bastion Remnant | INJECT |
| `mansion` | Woodland Mansion | INJECT |
| `end_city` | End City | INJECT |
| `custom_dungeon_example` | Custom Schematic | INJECT |
| `village` | Village | INJECT |
| `temple` | Desert Temple | INJECT |
| `jungle_temple` | Jungle Temple | INJECT |
| `igloo` | Igloo | INJECT |
| `shipwreck` | Shipwreck | INJECT |
| `monument` | Ocean Monument | INJECT |

#### Dungeon Config Format

```yaml
dungeons:
  stronghold:
    enabled: true
    loot:
      mode: INJECT                      # INJECT or REPLACE
      replaceChance: 0.15               # 15% chance (REPLACE mode only)
      vanillaItems:
        - item: DIAMOND
          weight: 15
          minCount: 1
          maxCount: 3
        - item: IRON_INGOT
          weight: 40
          minCount: 3
          maxCount: 8
      artifacts:
        - id: "fire_sword"
          weight: 10
          minCount: 1
          maxCount: 1
      blueprints:
        - recipeId: "basic_altar_craft_fire_sword"
          weight: 8
          amount: 1
      customItems:
        - id: "fire_core"
          weight: 20
          minCount: 1
          maxCount: 3
    bosses:
      enabled: true
      types:
        - entity: WITHER_SKELETON
          name: "<red>Flame Keeper"
          artifact: "fire_sword"
          blueprint: "basic_altar_craft_fire_sword"
          dropChance: 0.25
          health: 200.0
          equipment:
            mainhand: DIAMOND_SWORD
            helmet: NETHERITE_HELMET
          potionEffects:
            - effect: SPEED
              amplifier: 1
              duration: -1             # -1 = infinite
```

#### Loot Modes

| Mode | Description |
|---|---|
| `INJECT` | Adds custom loot alongside existing vanilla loot |
| `REPLACE` | Replaces a portion of existing loot based on `replaceChance` |

#### Boss Config Fields

| Field | Type | Description |
|---|---|---|
| `entity` | String | Entity type name (e.g., WITHER_SKELETON) |
| `name` | String | Custom boss name (MiniMessage) |
| `artifact` | String | Artifact ID to drop |
| `blueprint` | String | Blueprint recipe ID to drop |
| `dropChance` | Double | Chance of dropping items (0.0–1.0) |
| `health` | Double | Custom boss health |
| `equipment` | Map | Equipment slots (mainhand, offhand, helmet, chestplate, leggings, boots) |
| `potionEffects` | List | Permanent potion effects |

---

### 5.5 mob_loot.yml

Located at `plugins/WastelandArtifacts/mob_loot.yml`. Configures custom drops for **30+ mob types**.

#### Format

```yaml
mobs:
  BLAZE:
    enabled: true
    drops:
      - item: magma_core
        minAmount: 1
        maxAmount: 2
        chance: 0.7                # 70% chance
      - item: fire_core
        minAmount: 1
        maxAmount: 2
        chance: 0.4
```

#### Mob Matching Methods

The plugin supports 3 ways to match mobs:

##### 1. By EntityType (for vanilla mobs)
```yaml
BLAZE:
  drops:
    - item: magma_core
      chance: 0.7
```

##### 2. By Custom Name (for MythicMobs, EliteMobs, etc.)
```yaml
my_boss:
  displayName: "§cHell King"       # Partial match on custom name
  drops:
    - item: poseidon_flame
      chance: 0.8
```

##### 3. By Scoreboard Tags
```yaml
mythic_boss:
  tags:
    - "mythicmobs:boss"
  drops:
    - item: warden_heart
      chance: 0.5
```

#### Configured Mobs

BLAZE, WITHER_SKELETON, SKELETON, ZOMBIE, CREEPER, SPIDER, ENDERMAN, MAGMA_CUBE, GHAST, PHANTOM, WARDEN, ELDER_GUARDIAN, WITCH, HOGLIN, PIGLIN_BRUTE, ENDER_DRAGON, RAVAGER, EVOKER, VEX, PILLAGER, VINDICATOR, ZOMBIFIED_PIGLIN, BEE, DROWNED, STRIDER, WOLF

---

### 5.6 altars.yml

Located at `plugins/WastelandArtifacts/altars.yml`. Defines **3 altar tiers** with 3D multi-block structures and recipes.

#### Altar Structure Format

Altars use a layer-by-layer 3D grid pattern:

```yaml
structures:
  - layers:
      - "OOO"         # Y=0: floor
      - "OAO"         # A = activator block
      - "OOO"
      - "B B"         # Y=1: pillars/arches
      - "   "
      - "B B"
    mapping:
      O: OBSIDIAN
      A: CHISELED_STONE_BRICKS
      B: POLISHED_BLACKSTONE_BRICKS
```

Each character in the layer string maps to a block material via the `mapping` section. Spaces represent air blocks.

#### Altar Tiers

##### Basic Altar (Tier 1)
- **Activator:** CHISELED_STONE_BRICKS
- **Structure:** 3×3 base with arches
- **Global Cooldown:** 5 seconds
- **Recipes:** Fire Sword, Shadow Cloak

##### Advanced Altar (Tier 2)
- **Activator:** RESPAWN_ANCHOR
- **Structure:** 5×5 base with walls and columns
- **Global Cooldown:** 10 seconds
- **Recipes:** Ice Breaker, Necronomicon

##### Legendary Altar (Tier 3)
- **Activator:** RESPAWN_ANCHOR
- **Structure:** 5×5 base with netherite blocks
- **Global Cooldown:** 30 seconds
- **Recipes:** Warden Chestplate, Staff of Storms, Holy Grail, Wasteland Bag

#### Recipe Format

```yaml
recipes:
  - id: craft_fire_sword                    # Recipe ID (must be unique)
    result: fire_sword                       # Artifact ID to craft
    experience: 5                            # XP levels required
    cooldown: 10                             # Per-recipe cooldown in seconds
    catalyst:                                # Optional catalyst item (consumed)
      item: NETHER_STAR
      consume: true
    blueprintMaterial: PAPER                 # Blueprint item material
    blueprintName: "<red>📜 Blueprint: Fire Sword"  # Blueprint display name
    blueprintCustomModelData: 5001           # Blueprint custom model data
    ingredients:
      - type: DIAMOND_SWORD
        amount: 1
        slot: 5
      - type: BLAZE_ROD
        amount: 2
        slot: 4
      - type: BLAZE_POWDER
        amount: 4
        slot: 8
        name: "<red>Fire Core"              # Match by name
        customModelData: 6001               # Match by CMD
```

#### Settings Section

```yaml
settings:
  maxAltarsPerChunk: 1
  allowInAllWorlds: true
  worlds: []
  particlesEnabled: true
  soundsEnabled: true
  titleGUI: "<dark_gray>Artifact Altar"
  guiRows: 6
  previewMaxDistance: 10.0
```

#### Blueprint Recipe ID Format

For altar recipes that require a blueprint, the blueprint recipe ID used in the workbench follows this format:
```
<tier_name>_<recipe_id>
```

Example: `basic_altar_craft_fire_sword`

This is the recipe ID you use in `blueprint_workbench.yml` and reference in dungeon blueprints loot.

---

### 5.7 blueprint_workbench.yml

Located at `plugins/WastelandArtifacts/blueprint_workbench.yml`. Defines the workbench crafting recipes used to create altar blueprints.

```yaml
recipes:
  basic_altar_craft_fire_sword:
    enabled: true
    pattern:
      - " B "
      - "BSB"
      - " B "
    ingredients:
      B: BLAZE_ROD
      S: NETHERITE_SCRAP

  # Direct artifact crafting (crafts the artifact itself, not a blueprint)
  holy_grail_direct:
    enabled: true
    type: direct_artifact
    result: holy_grail
    pattern:
      - "AAA"
      - "ABA"
      - "AAA"
    ingredients:
      A: "artifact:unknown_relic"
      B: "artifact:unknown_eye"
```

#### Recipe Field Reference

| Field | Type | Description |
|---|---|---|
| `enabled` | Boolean | Enable/disable the recipe |
| `type` | String | `direct_artifact` or omitted (default: blueprint) |
| `result` | String | Artifact ID (for direct_artifact) |
| `pattern` | List | 3-row crafting pattern |
| `ingredients` | Map | Character-to-ingredient mapping |

#### Auto-Fallback

If a recipe is not defined in `blueprint_workbench.yml`, the plugin auto-generates a workbench recipe based on the altar tier:
- **Tier 1:** Bronze/Iron-level materials
- **Tier 2:** Diamond/Emerald materials
- **Tier 3:** Netherite/Diamond materials

#### Ingredient Resolution Order

When resolving ingredients in workbench recipes, the plugin checks:
1. `artifact:artifact_id` — Exact match of artifact item
2. `custom:item_id` — Match from `custom_items.yml`
3. `itemsadder:namespace:id` — ItemsAdder item
4. Plain material name — Vanilla `Material`
5. Extended format (with `type`, `name`, `lore`, `customModelData`) — `ItemStack.isSimilar()` match

---

### 5.8 rarities.yml

Located at `plugins/WastelandArtifacts/rarities.yml`. Defines **8 built-in rarities** that determine item name colors and decoration.

```yaml
rarities:
  COMMON:
    displayName: "<gray>Common"
    color: "#808080"
    decoration: ITALIC
    order: 0
  UNCOMMON:
    displayName: "<green>Uncommon"
    color: "#55FF55"
    decoration: ITALIC
    order: 1
  RARE:
    displayName: "<aqua>Rare"
    color: "#55FFFF"
    decoration: ITALIC
    order: 2
  EPIC:
    displayName: "<light_purple>Epic"
    color: "#FF55FF"
    decoration: ITALIC
    order: 3
  LEGENDARY:
    displayName: "<gold>Legendary"
    color: "#FFAA00"
    decoration: BOLD
    order: 4
  MYTHIC:
    displayName: "<dark_red>Mythic"
    color: "#AA0000"
    decoration: BOLD
    order: 5
  UNKNOWN:
    displayName: "<dark_aqua>Unknown"
    color: "#00AAAA"
    decoration: BOLD
    order: 6
  VOID:
    displayName: "<black>Void"
    color: "#000000"
    decoration: BOLD
    order: 7
```

| Field | Type | Description |
|---|---|---|
| `displayName` | String | Rarity display name (MiniMessage) |
| `color` | String | Hex color code (e.g., `#FFAA00`) |
| `decoration` | String | Text decoration: `BOLD`, `ITALIC`, `STRIKETHROUGH`, `UNDERLINE`, `MAGIC` |
| `order` | Integer | Sort order (higher = rarer/more valuable) |

You can add custom rarities by adding new entries:

```yaml
CUSTOM_LEGEND:
  displayName: "<gradient:gold:red>Legend"
  color: "#FFD700"
  decoration: BOLD
  order: 8
```

---

### 5.9 balance.yml

Located at `plugins/WastelandArtifacts/balance.yml`. Controls effect stacking limits, bag settings, and offhand behavior.

```yaml
stacking:
  maxAmplifier: 4                  # Global max amplifier for any effect
  enabled: true
  perEffect:
    SPEED:
      maxAmplifier: 3             # Speed capped at IV
    STRENGTH:
      maxAmplifier: 2             # Strength capped at III
    REGENERATION:
      maxAmplifier: 1             # Regeneration capped at II
    RESISTANCE:
      maxAmplifier: 2             # Resistance capped at III
    JUMP_BOOST:
      maxAmplifier: 3             # Jump Boost capped at IV
    HEALTH_BOOST:
      maxAmplifier: 4             # Health Boost capped at V
    ABSORPTION:
      maxAmplifier: 4             # Absorption capped at V

bag:
  maxSlots: 54                    # Maximum slots in bag (max 54)
  allowSameInBag: true            # Allow duplicate artifacts in bag
  effectApplyDelay: 20            # Ticks between effect recalculation

offhand:
  enabled: true                   # Artifacts work in offhand
  allowStacking: true             # Stack with bag effects
```

#### Effect Stacking Explained

When a player has multiple artifacts with the same `POTION_EFFECT_ON_EQUIP` effect, the amplifiers stack additively. For example:
- Artifact A: Speed I (amplifier 0)
- Artifact B: Speed II (amplifier 1)
- Result: Speed III (amplifier 2)

The `maxAmplifier` setting caps this stacking per effect. The global `maxAmplifier: 4` acts as an absolute cap.

---

### 5.10 Lang Files

Located at `plugins/WastelandArtifacts/lang/`. The plugin ships with **5 language files**:

| File | Language |
|---|---|
| `en_US.yml` | English (fallback) |
| `ru_RU.yml` | Russian |
| `de_DE.yml` | German |
| `fr_FR.yml` | French |
| `zh_CN.yml` | Chinese |

#### How Translations Work

Messages are stored in a flat key-value format with `String.format()` placeholder support (`%s`, `%d`, etc.):

```yaml
prefix: "<gradient:red:gold>Artifacts</gradient> <dark_gray>»</dark_gray>"
no-permission: "<red>No permission!"
artifact.not-found: "<red>Artifact '%s' not found!"
```

Player locale is automatically detected. If a translation key is missing in the player's locale, the plugin falls back to `en_US.yml`.

#### Adding a New Language

1. Create a new file in `lang/` (e.g., `lang/es_ES.yml`)
2. Copy the keys from `en_US.yml` and translate the values
3. Set `lang.locale: es_ES` in `config.yml`

---

### 5.11 features/*.yml

Located at `plugins/WastelandArtifacts/features/`. Each file corresponds to a feature module controlled via the `features` section in `config.yml`. If a module is disabled, its config is not loaded.

#### features/collection.yml

The artifact collection system. Tracks which artifacts the player has found and rewards progression.

```yaml
collection:
  enabled: true
  gui-title: "<dark_gray>📚 Artifact Collection"
  rewards:
    - percent: 25
      command: "give %player% diamond 1"
      message: "<gold>🎉 25% collection! +1 diamond"
    - percent: 50
      command: "give %player% netherite_ingot 1"
      message: "<gold>🎉 50% collection! +1 netherite"
    - percent: 75
      command: "give %player% enchanted_golden_apple 1"
      message: "<gold>🎉 75% collection! +1 enchanted golden apple"
    - percent: 100
      command: "give %player% nether_star 1"
      message: "<gold>🎉 100% collection! +1 nether star"
```

#### features/sets.yml

Artifact sets. When wearing multiple artifacts from the same set, the player receives bonuses.

```yaml
sets:
  fiery_set:
    enabled: true
    name: "<red>Fiery Set"
    artifacts:
      - "fire_sword"
      - "shadow_cloak"
      - "staff_of_storms"
    bonuses:
      - pieces: 2
        description: "<red>Fire Aura"
        effects:
          - "POTION:FIRE_RESISTANCE:0:999999"
          - "ATTRIBUTE:GENERIC_ATTACK_DAMAGE:2:0"
      - pieces: 3
        description: "<gold>Burning Wrath"
        effects:
          - "POTION:STRENGTH:1:999999"
          - "POTION:SPEED:1:999999"
          - "ATTRIBUTE:GENERIC_MAX_HEALTH:10:0"
```

#### features/abilities.yml

Active abilities for artifacts. Each ability has a type, cooldown, damage, radius, effects, and visual styling.

Supported ability types:

| Type | Description |
|---|---|
| `PROJECTILE` | Launches a projectile (e.g., fireball) |
| `TELEPORT` | Teleports the player forward |
| `DASH` | Dashes in the look direction |
| `SHIELD` | Grants absorption and resistance |
| `HEAL` | Restores health |
| `AOE` | Deals damage and applies effects in an area |
| `COMMAND` | Executes a command |

```yaml
abilities:
  fireball:
    enabled: true
    name: "<red>Fireball"
    cooldown: 10
    type: PROJECTILE
    projectile: SMALL_FIREBALL
    damage: 8
    radius: 0
    distance: 0
    duration: 0
    heal: 0
    knockback: 0
    command: ""
    effects:
      - type: FIRE
        amplifier: 0
        duration: 100
    particle: FLAME
    sound: ENTITY_BLAZE_SHOOT
    lore:
      - "<gray>Launches a fireball"
      - "<gray>Damage: <red>8"
      - "<gray>Cooldown: <white>10s"
```

#### features/upgrades.yml

Artifact upgrades by combining multiple copies of the same artifact.

```yaml
upgrades:
  enabled: true
  maxLevel: 10
  itemsPerUpgrade: 3
  damageMultiplier: 1.15
  healthMultiplier: 1.10
  keepOnDeath: false
  lore-format:
    - ""
    - "<gray>Level: <green>%level%"
    - "<gray>Damage multiplier: <gold>x%damage_mult%"
```

#### features/fishing_loot.yml

Custom fishing loot. Adds a chance to catch plugin items instead of regular fish.

```yaml
fishing:
  enabled: true
  entries:
    - item: "water_pearl"
      weight: 10
      minCount: 1
      maxCount: 2
      message: "<aqua>🐟 You caught a Water Pearl!"
```

#### features/elites.yml

Elite mobs — rare variants of regular mobs with boosted stats and special drops.

```yaml
elites:
  enabled: true
  spawnChance: 0.1
  types:
    - entity: ZOMBIE
      name: "<red>☠ Elite Zombie"
      healthMultiplier: 3.0
      damageMultiplier: 2.0
      dropChance: 0.5
      drops:
        - "soul_fragment"
        - "ancient_bone"
```

#### features/xp.yml

Artifact XP system. Artifacts gain XP from mob kills and scale their stats with each level.

```yaml
xp:
  enabled: true
  xpPerKill: 10
  xpPerLevel: 100
  maxLevel: 50
  damagePerLevel: 0.05
  levelUpSound: ENTITY_PLAYER_LEVELUP
  levelUpMessage: "<green>⬆ Artifact <gold>%artifact% <green>reached level <yellow>%level%!"
```

#### features/arena.yml

Boss arena (wave-based). The player fights waves of mobs and bosses.

```yaml
arena:
  enabled: true
  cooldown: 300
  arenaWorld: "arena"
  spawn:
    x: 0
    y: 64
    z: 0
  bossSpawn:
    x: 0
    y: 64
    z: 20
```

---

## 6. Integrations

Wasteland Artifacts integrates with other plugins through reflection, avoiding hard dependencies. If a plugin is not installed, its features are gracefully skipped.

### ItemsAdder

Allows using ItemsAdder items anywhere in the plugin configuration.

**Prefix:** `itemsadder:namespace:id`

```yaml
# In recipes:
ingredients:
  A: "itemsadder:my_namespace:my_item"

# In dungeon loot:
customItems:
  - id: "itemsadder:my_namespace:my_item"
    weight: 10
    minCount: 1
    maxCount: 3

# In mob drops:
drops:
  - item: "itemsadder:my_namespace:my_item"
    chance: 0.5
```

**Supported locations:**
- Altar recipe ingredients
- Workbench recipe ingredients
- Dungeon loot tables (customItems section)
- Mob loot drops

### Nexo

Integration with Nexo for using custom Nexo items. Works similarly to ItemsAdder via reflection.

**Prefix:** `nexo:item_id`

### Oraxen

Integration with Oraxen for using custom Oraxen items. Works via reflection.

**Prefix:** `oraxen:item_id`

### MythicMobs

Integration with MythicMobs. Allows matching elite mobs by their custom names and tags, and using MythicMobs in drop configurations.

### PlaceholderAPI

Adds placeholders for displaying artifact data, collection progress, XP, and arena statistics.

**Placeholder examples:**
- `%wastelandartifacts_collection_progress%` — collection progress
- `%wastelandartifacts_artifact_level%` — artifact level in hand
- `%wastelandartifacts_arena_waves%` — arena waves cleared

---

## 7. CraftingProtection

All custom items (artifacts, custom ingredients) are **protected from vanilla crafting** operations. The `CraftingProtectionListener` prevents custom items from being used in:

- **Crafting Table** — Custom items cannot be placed in any crafting recipe
- **Furnace / Blast Furnace / Smoker** — Cannot be smelted
- **Anvil** — Cannot be renamed or combined
- **Grindstone** — Cannot be disenchanted
- **Smithing Table** — Cannot be used in smithing templates

This protection applies to any item with:
- The `wastelandartifacts:artifact_id` PDC key (artifacts)
- The `wastelandartifacts:placed_custom_item` PDC key (custom items)
- Items matching entries in `CustomItemRegistry`

---

## 8. Dungeon Loot System

### How Loot Generation Works

The dungeon loot system uses multiple hooks to ensure custom loot appears in structure chests:

1. **Structure Generation Detection** (`AsyncStructureGenerateEvent`)
   - When a structure generates, the plugin tags nearby chests with `wastelandartifacts:dungeon_id` via PDC
   - Maps structure registry keys to dungeon config IDs

2. **Loot Table Injection** (`LootGenerateEvent`)
   - Intercepts vanilla loot table generation
   - Adds WA artifacts, blueprints, and custom items to the generated loot

3. **Inventory Open Hook** (`InventoryOpenEvent`)
   - Fallback for chest minecarts and already-generated chests
   - Checks for `wastelandartifacts:loot_populated` PDC key
   - If not populated, generates and injects loot
   - Also handles **boss spawning** (20% chance on first chest open)

### Structure Detection

The plugin maps vanilla structure types to dungeon configs:

```java
mapStructureToDungeon("stronghold")  → "stronghold"
mapStructureToDungeon("fortress")    → "fortress"
// ... 18 more mappings
```

### One-Time Generation

Each chest is marked with `wastelandartifacts:loot_populated` PDC to prevent duplicate loot generation. Once a chest has been populated, it won't be modified again.

### Boss Spawning

When a player opens a chest in a dungeon:
1. 20% chance to spawn a boss
2. Boss is configured in `dungeons/default.yml` under `bosses.types`
3. Boss has custom name, health, equipment, and potion effects
4. On death, drops artifacts and/or blueprints based on PDC-stored drop chances

### Custom Schematics

To add custom dungeons:
1. Place `.nbt` (Structure Block) or `.schem`/`.schematic` (WorldEdit) files in `plugins/WastelandArtifacts/schematics/`
2. Create a dungeon config entry in `dungeons/default.yml` or a new file in `dungeons/`
3. The dungeon ID must match the schematic filename (without extension)

---

## 9. Custom Item Block Placement

The `CustomItemBlockListener` handles placing and breaking custom items as blocks. Uses a combination of `ConcurrentHashMap` in memory and PDC on blocks for reliable tracking across all block types (not just `TileState`).

### Placing

When a player right-clicks to place a custom item (e.g., a custom block from `custom_items.yml`):
1. The listener stores the item ID in a `ConcurrentHashMap<BlockKey, String>` keyed by block coordinates
2. If the block supports `TileState`, the ID is also stored in the `wastelandartifacts:placed_custom_item` PDC key
3. One item is consumed from the player's hand

### Breaking

When a player breaks a placed custom block:
1. The listener first checks the in-memory `HashMap` for the item ID
2. If not found in memory, it checks `TileState` blocks for PDC
3. If the ID is found, drops the custom item instead of the vanilla block drop
4. The custom item retains its original NBT data (name, lore, CMD)

The dual storage (HashMap + PDC) ensures correct behavior for all block types, including those without `TileState` (e.g., sand, gravel, regular blocks).

---

## 10. Database

Wasteland Artifacts uses HikariCP for database connections. Supports SQLite (default) and MySQL.

### Configuration

Database settings are configured in `config.yml` under the `database` section:

```yaml
database:
  enabled: true
  type: SQLITE        # SQLITE or MYSQL
  host: localhost
  port: 3306
  database: wasteland_artifacts
  user: root
  password: ""
  poolSize: 10
```

### Database Types

| Type | File/Host | Connection Pool |
|---|---|---|
| `SQLITE` | `plugins/WastelandArtifacts/data.db` | 1 (SQLite does not support concurrent writes) |
| `MYSQL` | `host:port/database` | Configurable via `poolSize` (default 10) |

### Table Structure

The plugin uses 4 tables:

#### wa_players

Stores basic player information.

```sql
CREATE TABLE IF NOT EXISTS wa_players (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(16),
    last_seen BIGINT,
    total_found INT DEFAULT 0
);
```

#### wa_artifact_data

Stores artifact data (level, XP, kills).

```sql
CREATE TABLE IF NOT EXISTS wa_artifact_data (
    id VARCHAR(64),
    owner_uuid VARCHAR(36),
    level INT DEFAULT 1,
    xp BIGINT DEFAULT 0,
    kills INT DEFAULT 0,
    slot INT DEFAULT -1,
    PRIMARY KEY (id, owner_uuid)
);
```

#### wa_collection

Tracks which artifacts the player has found.

```sql
CREATE TABLE IF NOT EXISTS wa_collection (
    player_uuid VARCHAR(36),
    artifact_id VARCHAR(64),
    found_date BIGINT,
    PRIMARY KEY (player_uuid, artifact_id)
);
```

#### wa_arena_stats

Player statistics on the boss arena.

```sql
CREATE TABLE IF NOT EXISTS wa_arena_stats (
    player_uuid VARCHAR(36) PRIMARY KEY,
    waves_cleared INT DEFAULT 0,
    bosses_killed INT DEFAULT 0,
    best_time BIGINT DEFAULT 0
);
```

### Feature Modules with Database Dependencies

Some feature modules require the database:

- **Collection** (`collection`) — stores player progression
- **Upgrades** (`upgrades`) — stores artifact levels
- **Artifact XP** (`artifactXP`) — stores artifact experience
- **Boss Arena** (`bossArena`) — stores player statistics

If `database.enabled: false`, these modules are automatically disabled.

---

## 11. API

### Getting the API Instance

```java
WastelandArtifactsAPI api = WastelandArtifacts.getInstance().getApi();
```

### Available Methods

```java
// Artifact Registration
void registerArtifact(@NotNull Artifact artifact)
void unregisterArtifact(@NotNull String id)
Artifact getArtifact(@NotNull String id)
List<Artifact> getAllArtifacts()

// Item Creation
ItemStack createItem(@NotNull Artifact artifact)
ItemStack createItem(@NotNull String artifactId)

// Artifact Detection
Artifact getArtifactFromItem(@NotNull ItemStack item)
boolean isArtifact(@NotNull ItemStack item)

// Giving Artifacts
void giveArtifact(@NotNull Player player, @NotNull String artifactId, int amount)

// Component & Trigger Registration
void registerComponent(@NotNull String id, @NotNull Class<? extends ArtifactComponent> clazz)
void registerTriggerType(@NotNull TriggerType type, @NotNull Trigger trigger)

// Reload
void reload()

// Plugin Instance
WastelandArtifacts getPlugin()
```

### Example Usage

```java
// Get the API
WastelandArtifactsAPI api = WastelandArtifacts.getInstance().getApi();

// Create and give an artifact
api.giveArtifact(player, "fire_sword", 1);

// Check if an item is an artifact
if (api.isArtifact(itemStack)) {
    Artifact artifact = api.getArtifactFromItem(itemStack);
}

// Create an item stack from an artifact
ItemStack item = api.createItem("warden_chestplate");

// Register a custom artifact
Artifact customArtifact = Artifact.builder("my_artifact")
    .displayName("<gold>My Custom Artifact")
    .baseItem(Material.NETHERITE_SWORD)
    .customModelData(9001)
    .rarity(Rarity.LEGENDARY)
    .components(
        new DamageComponent(15.0),
        new FireAspectComponent(2)
    )
    .build();
api.registerArtifact(customArtifact);
```

---

## 12. Troubleshooting

### Common Issues and Solutions

#### Resource pack is not being sent to players
- Ensure `autoHost: true` in `config.yml`
- Check that port 8192 is not blocked by a firewall
- Try `/waadmin rp build` then `/waadmin rp send`

#### Artifacts have no custom model/texture
- Resource pack must be accepted by the player
- Run `/waadmin rp build` to regenerate model files
- Check `customModelData` values for conflicts with other plugins

#### Altar crafting is not working
- Verify the altar structure matches exactly (use `/altar preview`)
- Check that you have the required blueprint in your inventory
- Ensure you drop items (Q key) on the altar blocks, not right-click
- Check the altar's `globalCooldown` and per-recipe `cooldown`

#### Blueprint recipes are not showing up
- Ensure `crafting.enabled: true` in `config.yml`
- Blueprints are auto-discovered on player join
- Verify the recipe ID format: `<tier>_<recipe>`

#### Dungeon loot is not being injected
- Check `dungeons.enabled: true` in `config.yml`
- Run `/dungeon scan` to populate structure cache
- Ensure `lootInjection: true`
- For existing worlds, structures must be rescanned

#### Mob drops are not working
- Verify the mob type is correctly spelled (EntityType name)
- For custom name matching, ensure the config `displayName` matches the mob's custom name
- Check that `chance` values are between 0.0 and 1.0

#### Plugin errors on startup
- Ensure you are using **Java 21** or higher
- Ensure you are using **Paper 1.21.1** or higher (not Spigot/CraftBukkit)
- Check for conflicting plugins using similar NBT/PDC keys

#### Database is not connecting
- Check `database.enabled: true` in `config.yml`
- For MySQL, ensure the host and port are accessible
- Check server logs for SQL errors

#### Features are not loading
- Ensure the corresponding settings in the `features` section are enabled
- Some modules (collection, upgrades, xp, arena) require the database to be enabled
- Verify that files in `features/` exist and have valid YAML

### Enabling Debug Mode

Use `/waadmin debug` to view:
- Number of registered artifacts
- Number of registered components
- Number of dungeon configs loaded
- Number of schematics loaded
- Resource pack server status

### Log Files

Check `logs/latest.log` for:
- Plugin initialization messages (including load time in ms)
- Integration status (ItemsAdder, Nexo, Oraxen, MythicMobs, PlaceholderAPI)
- Dungeon scanning progress
- Resource pack generation status
- Any errors with YAML parsing
- Database connection status

---

> **Wasteland Artifacts v2.1.0** — Created by animesao for Paper 1.21.11+.
