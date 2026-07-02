# 🏜️ Wasteland Artifacts

**A Paper 1.21.1 plugin | Craft, find, and customize artifacts with unique effects**

[![Paper](https://img.shields.io/badge/Paper-1.21.1-blue?logo=paper)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![Build](https://github.com/animesao/WA/actions/workflows/build.yml/badge.svg)](https://github.com/animesao/WA/actions)

---

**🌐 English • [Русский](#-описание)**

[![Paper](https://img.shields.io/badge/Paper-1.21.1-blue?logo=paper)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![Build](https://github.com/animesao/WA/actions/workflows/build.yml/badge.svg)](https://github.com/animesao/WA/actions)

---

## 📋 Описание

**Wasteland Artifacts v2.1.0** — мощный плагин для Paper, добавляющий систему кастомных артефактов с уникальными эффектами, много-блочные алтари для крафта, чертежи, подземелья с лутом, дроп с мобов, сумку для артефактов и полную настройку через YAML.

Плагин создан для **Russian Wasteland** тематики: пустоши, древние реликвии, неизведанные силы. Всё настраивается — от редкостей до эффектов и рецептов.

**v2.1.0** добавляет систему фич (FeatureManager), коллекцию артефактов, сет-бонусы, активные способности, улучшение предметов, рыбалку, элитных мобов, XP артефактов, арену боссов, интеграцию с Nexo/Oraxen/MythicMobs/PlaceholderAPI и AdminItemsGUI.

---

## ✨ Возможности

### 🏛️ Алтари (3D multi-block structures)
- **3 уровня алтарей**: Basic, Advanced, Legendary
- **3D-структуры** с колоннами, арками, этажами
- `/altar preview` — предпросмотр призрачными блоками (BlockDisplay)
- `/altar build` — авто-постройка из инвентаря
- Крафт броском предметов на алтарь (Ingredient drop)

### 📜 Чертежи (Blueprints)
- Брось чертёж на алтарь → задаётся рецепт
- Настраиваемый внешний вид (материал, имя, CMD)
- Workbench рецепты для крафта чертежей
- Отдельный конфиг `blueprint_workbench.yml`
- Рецепты открываются в книге крафта

### ⚔️ Артефакты
- Оружие, броня, аксессуары, книги, инструменты
- 8 редкостей: Common, Uncommon, Rare, Epic, Legendary, Mythic, **Unknown**, **Void**
- 16+ типов компонентов: урон, огонь, молнии, вампиризм, призыв, взрыв, снаряды, АОЕ
- Эффекты при надевании (Potion Effects + Attributes)
- Кастомные скины через player head (Base64)

### 🎒 Сумка артефактов (/bag)
- 54 слота для хранения артефактов
- Эффекты суммируются и стакаются с лимитами
- Для открытия нужен артефакт **"Сумка Пустоши"**
- Поддержка offhand (левой руки)

### 🗡️ Дроп с мобов
- 30+ ванильных мобов с кастомным дропом
- Поддержка кастомных мобов (MythicMobs, EliteMobs)
- Матчинг по EntityType, customName, scoreboardTags

### 🏚️ Лут в подземельях
- Артефакты и чертежи в сундуках структур
- Stronghold, Fortress, Ancient City, Bastion, Mansion, End City
- Режимы: INJECT, REPLACE, ADD_CHEST
- Боссы с дропом и кастомной экипировкой

### 🧩 86 кастомных предметов (компонентов)
- Элементы, части существ, древние реликвии, алхимия
- Космические, мифические, материалы пустоши
- CMD 6001–6086
- Используются в рецептах алтарей

### 🎮 Система фич (FeatureManager)
- **12 features**: Collection, Sets, Abilities, Upgrades, Fishing, Elites, XP, Arena, PlaceholderAPI, MythicMobs, Nexo, Oraxen
- Каждая включается/выключается в `config.yml`
- Независимая загрузка и регистрация

### 📚 Коллекция артефактов
- `/artifact collection` — GUI коллекции
- Отслеживание найденных артефактов в БД
- Прогресс: `X / Y` найденных артефактов

### 🔗 Сеты артефактов
- Бонусы за набор определённых артефактов
- Настраиваются в `features/sets.yml`
- Многоуровневые бонусы (2/3/4+ pieces)

### ⚡ Активные способности
- **6 типов**: PROJECTILE, TELEPORT, DASH, SHIELD, HEAL, AOE, COMMAND
- Настраиваются в `features/abilities.yml`
- Урон, радиус, дальность, откат, левел, партиклы

### ⬆️ Улучшение артефактов
- Апгрейд комбинированием копий
- Настраиваемые уровни и шансы
- Конфиг `features/upgrades.yml`

### 🎣 Рыбалка
- Кастомный лут при ловле рыбы
- Конфиг `features/fishing_loot.yml`

### 👹 Элитные мобы
- Мобы с множителями здоровья/урона/скорости
- Кастомный дроп
- Конфиг `features/elites.yml`

### 💎 XP артефактов
- XP за убийства мобов с артефактом в руке
- Уровни и масштабирование урона
- Конфиг `features/xp.yml`

### 🏟️ Арена боссов
- Волновая арена с наградами
- `/arena` — открыть GUI
- Конфиг `features/arena.yml`

### 🔧 Полная настройка
- Все редкости — через `rarities.yml`
- Все предметы — через `custom_items.yml`
- Все рецепты — через `altars.yml` и `blueprint_workbench.yml`
- Весь дроп — через `mob_loot.yml`
- Все сообщения — через `lang/*.yml`
- Баланс стакания — через `balance.yml`

### 🌐 Локализация
- 5 языков: Русский, English, Deutsch, Français, 中文
- Авто-определение языка игрока
- Все сообщения настраиваются

### 🗄️ База данных
- 4 таблицы: `wa_players`, `wa_artifact_data`, `wa_collection`, `wa_arena_stats`
- SQLite по умолчанию, MySQL опционально (HikariCP)

---

## 📦 Установка

1. Скачай последний [релиз](https://github.com/animesao/WA/releases)
2. Помести `WastelandArtifacts-v2.1.0.jar` в папку `plugins/`
3. Убедись что на сервере **Paper 1.21.1** и **Java 21+**
4. Перезапусти сервер
5. Настрой конфиги в `plugins/WastelandArtifacts/`

### Зависимости
- **Paper 1.21.1** (обязательно)
- **WorldEdit** (опционально, для схем)
- **FastAsyncWorldEdit** (опционально)
- **PlaceholderAPI** (опционально, для плейсхолдеров)
- **MythicMobs** (опционально, для кастомных мобов)
- **Nexo / Oraxen / ItemsAdder** (опционально, для кастомных текстур)

---

## 🎮 Команды

### Игроки
| Команда | Описание | Право |
|---------|----------|-------|
| `/artifact list [page]` | Список артефактов | `player.artifact` |
| `/artifact info <id>` | Инфо об артефакте | `player.artifact` |
| `/artifact collection` | Коллекция артефактов | `player.collection` |
| `/altar list` | Список алтарей | `player.altar` |
| `/altar info <tier>` | Инфо + рецепты | `player.altar` |
| `/altar preview <tier>` | Предпросмотр | `player.altar` |
| `/bag` | Сумка артефактов | `player.bag` |
| `/arena` | Арена боссов | `player.arena` |

### Админы
| Команда | Описание | Право |
|---------|----------|-------|
| `/artifact give <id> [player]` | Выдать артефакт | `admin.blueprint` |
| `/artifact reload` | Перезагрузить конфиги | `admin` |
| `/waadmin gui` | GUI выдачи предметов (Artifacts/Items/Blueprints) | `admin.gui` |
| `/waadmin blueprint <id>` | Выдать чертёж | `admin.blueprint` |
| `/waadmin customitem <id>` | Выдать кастомный предмет | `admin.customitem` |
| `/waadmin rp build` | Собрать ресурс-пак | `admin.rp` |
| `/waadmin rp send` | Раздать ресурс-пак | `admin.rp` |
| `/waadmin debug` | Отладка | `admin.debug` |
| `/altar build <tier>` | Построить алтарь | `admin.altar` |
| `/altar schematic save` | Сохранить схему | `admin.altar` |
| `/dungeon loot` | GUI лута | `admin` |
| `/item encode` | Base64 предмета в руке | `admin` |

---

## 🔐 Permissions

| Право | По умолчанию | Описание |
|-------|-------------|----------|
| `wastelandartifacts.*` | OP | Все права |
| `wastelandartifacts.admin` | OP | Админ-команды |
| `wastelandartifacts.player` | Все | Игрок-команды |
| `wastelandartifacts.admin.altar` | OP | Build + schematic |
| `wastelandartifacts.admin.blueprint` | OP | Выдача чертежей |
| `wastelandartifacts.admin.customitem` | OP | Выдача кастомных предметов |
| `wastelandartifacts.admin.debug` | OP | Отладка |
| `wastelandartifacts.admin.rp` | OP | Ресурс-пак |
| `wastelandartifacts.admin.gui` | OP | GUI выдачи |
| `wastelandartifacts.player.altar` | Все | Просмотр алтарей |
| `wastelandartifacts.player.bag` | Все | Сумка артефактов |
| `wastelandartifacts.player.artifact` | Все | Просмотр артефактов |
| `wastelandartifacts.player.blueprint` | Все | Использование чертежей |
| `wastelandartifacts.player.collection` | Все | Коллекция артефактов |
| `wastelandartifacts.player.arena` | Все | Арена боссов |

---

## 📁 Структура конфигов

```
plugins/WastelandArtifacts/
├── config.yml                 # Основной конфиг + фичи
├── altars.yml                 # Алтари + рецепты
├── blueprints.yml             # Крафт чертежей
├── custom_items.yml           # 86 кастомных предметов (6001–6086)
├── mob_loot.yml               # Дроп с мобов
├── balance.yml                # Баланс стакания
├── rarities.yml               # Настройка редкостей
├── blueprint_workbench.yml    # Workbench рецепты чертежей
├── features/
│   ├── collection.yml         # Коллекция артефактов
│   ├── sets.yml               # Сет-бонусы
│   ├── abilities.yml          # Активные способности
│   ├── upgrades.yml           # Улучшение артефактов
│   ├── fishing_loot.yml       # Рыбалка — кастомный лут
│   ├── elites.yml             # Элитные мобы
│   ├── xp.yml                 # XP артефактов
│   └── arena.yml              # Арена боссов
├── lang/                      # Локализация
│   ├── ru_RU.yml
│   ├── en_US.yml
│   ├── de_DE.yml
│   ├── fr_FR.yml
│   └── zh_CN.yml
├── artifacts/
│   └── examples.yml           # Определения артефактов
├── dungeons/
│   └── default.yml            # Лут в структурах
└── schematics/                # Схемы .nbt
```

---

## 🏛️ Алтари — краткое руководство

1. **Построй алтарь:** `/altar preview basic_altar` → поставь блоки вручную
2. **Получи чертёж:** `/waadmin blueprint basic_altar_craft_fire_sword`
3. **Брось чертёж (Q)** на алтарь → появится холограмма
4. **Брось ингредиенты** в указанные слоты
5. **Авто-крафт:** когда все ингредиенты собраны
6. **Забери предметы:** Shift+ПКМ по активатору
7. **Сумка:** `/bag` (нужна Сумка Пустоши)

### Пример структуры Basic Altar:
```
Y=2:    · ■ ·         ← колонна
Y=1:  ■ · · · ■       ← арки
Y=0:  O O O           ← пол + активатор
      O A O           A = Chiseled Stone Bricks
      O O O           O = Obsidian, B = Blackstone Bricks
```

---

## ⚙️ Примеры конфигов

### Артефакт (examples.yml)
```yaml
  - id: "fire_sword"
    displayName: "<red>Огненный Клинок"
    baseItem: DIAMOND_SWORD
    customModelData: 1001
    rarity: RARE
    components:
      - type: DAMAGE
        damage: 12.0
      - type: FIRE_ASPECT
        level: 2
      - type: LIGHTNING
        chance: 0.15
      - type: LIFE_STEAL
        percentage: 0.15
```

### Рецепт алтаря (altars.yml)
```yaml
    recipes:
      - id: craft_fire_sword
        result: fire_sword
        experience: 5
        ingredients:
          - type: DIAMOND_SWORD
            amount: 1
            slot: 5
          - type: BLAZE_ROD
            amount: 3
            slot: 1
          - type: BLAZE_POWDER
            amount: 2
            slot: 4
            name: "<red>Огненное Ядро"
            customModelData: 6001
```

### Редкость (rarities.yml)
```yaml
  UNKNOWN:
    displayName: "<dark_aqua>Неизвестный"
    color: "#00AAAA"
    decoration: BOLD
    order: 6
  VOID:
    displayName: "<black>Пустота"
    color: "#000000"
    decoration: BOLD
    order: 7
```

### Дроп с моба (mob_loot.yml)
```yaml
  BLAZE:
    drops:
      - item: magma_core
        chance: 0.7
      - item: poseidon_flame
        chance: 0.05
  WITHER_SKELETON:
    displayName: "§cКороль Ада"
    drops:
      - item: wither_skull
        chance: 0.5
```

### Активная способность (features/abilities.yml)
```yaml
abilities:
  fire_blast:
    enabled: true
    name: "<red>Огненный Взрыв"
    type: AOE
    cooldown: 15
    damage: 8.0
    radius: 5.0
    particle: FLAME
    sound: ENTITY_BLAZE_SHOOT
    lore:
      - "<gray>Поджигает всех врагов вокруг"
```

---

## 🧑‍💻 API для разработчиков

```java
WastelandArtifactsAPI api = WastelandArtifacts.getAPI();

// Получить артефакт
Artifact artifact = api.getArtifact("fire_sword");

// Создать ItemStack
ItemStack item = api.createItem(artifact);

// Проверить предмет
boolean isArt = api.isArtifact(item);

// Выдать игроку
api.giveArtifact(player, "fire_sword", 1);

// Зарегистрировать свой компонент
api.registerComponent("my_comp", MyComponent.class);

// Перезагрузить конфиги
api.reload();
```

---

## 🛠️ Сборка из исходников

```bash
git clone https://github.com/animesao/WA.git
cd WastelandArtifacts
./gradlew build
```

JAR будет в `build/libs/WastelandArtifacts-v2.1.0.jar`

**Требования:**
- JDK 21+
- Gradle 8.10 (Kotlin DSL)

---

## 📄 Лицензия

MIT License — см. файл [LICENSE](LICENSE).

---

## 👨‍💻 Автор

**animesao** — создатель Wasteland Artifacts

---

## 🙏 Благодарности

- PaperMC за отличный API
- Kyori за Adventure (MiniMessage)
- Jackson за YAML-парсер
- HikariCP за пул соединений

---

# 🇬🇧 English Documentation

# 🏜️ Wasteland Artifacts

**A Paper 1.21.1 plugin | Craft, find, drop, and customize artifacts with unique effects**

---

## 📋 Overview

**Wasteland Artifacts v2.1.0** is a powerful Paper plugin that adds a complete artifact system: custom items with unique effects, multi-block altars for crafting, blueprints, dungeon loot, mob drops, an artifact bag (/bag), and full YAML configuration.

Everything is configurable — rarities, effects, attributes, recipes, loot tables, messages, and balance. Supports custom textures from Nexo, Oraxen, and ItemsAdder.

**v2.1.0** introduces the FeatureManager (12 features), artifact collection, set bonuses, active abilities, item upgrades, fishing loot, elite mobs, artifact XP, boss arena, Nexo/Oraxen/MythicMobs/PlaceholderAPI integration, and AdminItemsGUI.

---

## ✨ Features

### 🏛️ 3D Multi-Block Altars
- 3 tiers: Basic, Advanced, Legendary
- True 3D structures with pillars, arches, layers
- `/altar preview` — ghost block preview (BlockDisplay)
- `/altar build` — auto-build from inventory
- Drop items onto the altar to craft

### 📜 Blueprints
- Drop a blueprint on the altar → sets the recipe
- Customizable appearance (material, name, CMD)
- Workbench recipes for blueprints
- Recipes auto-discovered in recipe book

### ⚔️ Artifacts
- Weapons, armor, accessories, books, tools
- 8 rarities: Common → Uncommon → Rare → Epic → Legendary → Mythic → **Unknown** → **Void**
- 16+ component types: damage, fire, lightning, lifesteal, summon, explosion, projectile, AOE, etc.
- Equip effects (potion effects + attributes)
- Custom player head textures (Base64)

### 🎒 Artifact Bag (/bag)
- 54 slots to store artifacts
- Effects stack with configurable limits
- Requires the **"Wasteland Bag"** artifact to use
- Offhand (left hand) support

### 🗡️ Mob Drops
- 30+ vanilla mobs with custom loot
- Custom mob support (MythicMobs, EliteMobs)
- Matching by EntityType, customName, scoreboardTags

### 🏚️ Dungeon Loot
- Artifacts and blueprints in structure chests
- Supports: Stronghold, Fortress, Ancient City, Bastion, Mansion, End City
- Modes: INJECT, REPLACE, ADD_CHEST
- Custom bosses with equipment and drops

### 🧩 86 Custom Items (Components)
- Elements, creature parts, ancient relics, alchemy
- Cosmic, mythical, wasteland materials
- CMD 6001–6086
- Used in altar recipes

### 🎮 Feature System (FeatureManager)
- **12 features**: Collection, Sets, Abilities, Upgrades, Fishing, Elites, XP, Arena, PlaceholderAPI, MythicMobs, Nexo, Oraxen
- Toggle each in `config.yml`
- Independent loading and registration

### 📚 Artifact Collection
- `/artifact collection` — GUI collection
- Tracks discovered artifacts in DB
- Progress: `X / Y` artifacts found

### 🔗 Artifact Sets
- Set bonuses for wearing matching artifacts
- Configured in `features/sets.yml`
- Tiered bonuses (2/3/4+ pieces)

### ⚡ Active Abilities
- **6 types**: PROJECTILE, TELEPORT, DASH, SHIELD, HEAL, AOE, COMMAND
- Configured in `features/abilities.yml`
- Damage, radius, range, cooldown, particles

### ⬆️ Artifact Upgrades
- Upgrade by combining duplicates
- Configurable levels and chances
- Config: `features/upgrades.yml`

### 🎣 Fishing
- Custom loot when fishing
- Config: `features/fishing_loot.yml`

### 👹 Elite Mobs
- Mobs with health/damage/speed multipliers
- Custom drops
- Config: `features/elites.yml`

### 💎 Artifact XP
- XP for killing mobs with an artifact equipped
- Levels and damage scaling
- Config: `features/xp.yml`

### 🏟️ Boss Arena
- Wave-based arena with rewards
- `/arena` — open GUI
- Config: `features/arena.yml`

### 🔧 Full Configuration
- Rarities → `rarities.yml`
- Items → `custom_items.yml`
- Recipes → `altars.yml` + `blueprint_workbench.yml`
- Mob drops → `mob_loot.yml`
- Messages → `lang/*.yml` (5 languages)
- Balance → `balance.yml`

### 🌐 Localization
- 5 languages: English, Russian, German, French, Chinese
- Auto-detects player locale
- Every message customizable

### 🗄️ Database
- 4 tables: `wa_players`, `wa_artifact_data`, `wa_collection`, `wa_arena_stats`
- SQLite by default, MySQL optional (HikariCP)

---

## 📦 Installation

1. Download the latest [release](https://github.com/animesao/WA/releases)
2. Place `WastelandArtifacts-v2.1.0.jar` in your `plugins/` folder
3. Server must run **Paper 1.21.1** with **Java 21+**
4. Restart the server
5. Configure files in `plugins/WastelandArtifacts/`

### Dependencies
- **Paper 1.21.1** (required)
- **WorldEdit** (optional, for schematics)
- **FastAsyncWorldEdit** (optional)
- **PlaceholderAPI** (optional, for placeholders)
- **MythicMobs** (optional, for custom mobs)
- **Nexo / Oraxen / ItemsAdder** (optional, for custom textures)

---

## 🎮 Commands

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/artifact list [page]` | List all artifacts | `player.artifact` |
| `/artifact info <id>` | Artifact info | `player.artifact` |
| `/artifact collection` | Artifact collection | `player.collection` |
| `/altar list` | List altars | `player.altar` |
| `/altar info <tier>` | Info + recipes | `player.altar` |
| `/altar preview <tier>` | Preview structure | `player.altar` |
| `/bag` | Artifact bag | `player.bag` |
| `/arena` | Boss arena | `player.arena` |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/artifact give <id> [player]` | Give artifact | `admin.blueprint` |
| `/artifact reload` | Reload configs | `admin` |
| `/waadmin gui` | Item admin GUI (Artifacts/Items/Blueprints) | `admin.gui` |
| `/waadmin blueprint <id>` | Give blueprint | `admin.blueprint` |
| `/waadmin customitem <id>` | Give custom item | `admin.customitem` |
| `/waadmin rp build` | Build resource pack | `admin.rp` |
| `/waadmin rp send` | Send resource pack | `admin.rp` |
| `/waadmin debug` | Debug info | `admin.debug` |
| `/altar build <tier>` | Auto-build altar | `admin.altar` |
| `/altar schematic save` | Save schematic | `admin.altar` |
| `/dungeon loot` | Loot GUI | `admin` |
| `/item encode` | Get item Base64 | `admin` |

---

## 🔐 Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `wastelandartifacts.*` | OP | All permissions |
| `wastelandartifacts.admin` | OP | All admin commands |
| `wastelandartifacts.player` | All | All player commands |
| `wastelandartifacts.admin.altar` | OP | Build + schematics |
| `wastelandartifacts.admin.blueprint` | OP | Give blueprints |
| `wastelandartifacts.admin.customitem` | OP | Give custom items |
| `wastelandartifacts.admin.debug` | OP | Debug |
| `wastelandartifacts.admin.rp` | OP | Resource pack |
| `wastelandartifacts.admin.gui` | OP | Admin items GUI |
| `wastelandartifacts.player.altar` | All | View altars |
| `wastelandartifacts.player.bag` | All | Artifact bag |
| `wastelandartifacts.player.artifact` | All | View artifacts |
| `wastelandartifacts.player.blueprint` | All | Use blueprints |
| `wastelandartifacts.player.collection` | All | Artifact collection |
| `wastelandartifacts.player.arena` | All | Boss arena |

---

## 📁 Config Structure

```
plugins/WastelandArtifacts/
├── config.yml                 # Main config + features toggle
├── altars.yml                 # Altars + recipes
├── custom_items.yml           # 86 custom items (6001–6086)
├── mob_loot.yml               # Mob drops
├── balance.yml                # Stacking balance
├── rarities.yml               # Rarity settings
├── blueprint_workbench.yml    # Workbench recipes
├── features/
│   ├── collection.yml         # Artifact collection
│   ├── sets.yml               # Set bonuses
│   ├── abilities.yml          # Active abilities
│   ├── upgrades.yml           # Artifact upgrades
│   ├── fishing_loot.yml       # Fishing loot
│   ├── elites.yml             # Elite mobs
│   ├── xp.yml                 # Artifact XP
│   └── arena.yml              # Boss arena
├── lang/                      # Localization
│   ├── en_US.yml
│   ├── ru_RU.yml
│   ├── de_DE.yml
│   ├── fr_FR.yml
│   └── zh_CN.yml
├── artifacts/
│   └── examples.yml           # Artifact definitions
├── dungeons/
│   └── default.yml            # Dungeon loot
└── schematics/                # .nbt schematics
```

---

## 🏛️ Quick Altar Guide

1. **Build an altar:** `/altar preview basic_altar` → place blocks manually
2. **Get a blueprint:** `/waadmin blueprint basic_altar_craft_fire_sword`
3. **Drop the blueprint (Q)** on the altar → hologram appears
4. **Drop ingredients** into the shown slots
5. **Auto-craft** when all ingredients are present
6. **Collect items:** Shift+Right-click the activator block
7. **Artifact Bag:** `/bag` (requires Wasteland Bag artifact)

---

## 🛠️ Building from Source

```bash
git clone https://github.com/animesao/WA.git
cd WastelandArtifacts
./gradlew build
```

JAR will be at `build/libs/WastelandArtifacts-v2.1.0.jar`

**Requirements:**
- JDK 21+
- Gradle 8.10 (Kotlin DSL)

---

## 👨‍💻 Author

**animesao**

---

## 📄 License

MIT License — see [LICENSE](LICENSE).

---

## 🙏 Credits

- PaperMC for the amazing API
- Kyori for Adventure (MiniMessage)
- Jackson for YAML parsing
- HikariCP for connection pooling
