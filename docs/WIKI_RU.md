# Wasteland Artifacts — Полная вики

> **Версия:** v2.1.0 | **Платформа:** Paper 1.21.11 | **Java:** 21

---

## Содержание

1. [Обзор](#1-обзор)
2. [Установка](#2-установка)
3. [Команды](#3-команды)
4. [Права](#4-права)
5. [Файлы конфигурации](#5-файлы-конфигурации)
   - [config.yml](#51-configyml)
   - [custom_items.yml](#52-custom_itemsyml)
   - [artifacts/examples.yml](#53-artifactsexamplesyml)
   - [dungeons/default.yml](#54-dungeonsdefaultyml)
   - [mob_loot.yml](#55-mob_lootyml)
   - [altars.yml](#56-altarsyml)
   - [blueprint_workbench.yml](#57-blueprint_workbenchyml)
   - [rarities.yml](#58-raritiesyml)
   - [balance.yml](#59-balanceyml)
   - [lang/*.yml](#510-языковые-файлы)
   - [features/*.yml](#511-featuresyml)
6. [Интеграции](#6-интеграции)
7. [Защита крафта](#7-защита-крафта)
8. [Система подземельной добычи](#8-система-подземельной-добычи)
9. [Размещение кастомных блоков](#9-размещение-кастомных-блоков)
10. [База данных](#10-база-данных)
11. [API](#11-api)
12. [Решение проблем](#12-решение-проблем)

---

## 1. Обзор

**Wasteland Artifacts** — это премиум-плагин для Minecraft Paper 1.21.11, добавляющий на ваш сервер полноценную систему артефактов. Включает кастомные артефакты с уникальными способностями, подземельную добычу, 3D-алтари для крафта, систему выпадения с мобов, систему боссов, коллекцию артефактов, активные способности, улучшение предметов, рыбалку, элитных мобов и арену боссов.

### Основные возможности

- **30+ кастомных артефактов** — оружие, броня, инструменты, аксессуары с уникальными способностями (огненный аспект, молнии, вампиризм, взрывы, призывы, снаряды, АОЕ и др.)
- **86 кастомных ингредиентов** — компоненты для крафта, разделённые на категории (Элементы, Части существ, Древние артефакты, Алхимия, Пустошь, Космос, Материалы, Мифические, Особые, Добавленные)
- **3 уровня алтарей** — 3D-мультиблочные структуры для крафта артефактов (Базовый, Продвинутый, Легендарный)
- **Система чертежей** — создавайте чертежи в обычном верстаке, используйте их на алтарях
- **Подземельная добыча** — 20+ типов ванильных структур с кастомными таблицами лута
- **Система боссов** — кастомные боссы, появляющиеся при открытии сундуков в подземельях
- **Добыча с мобов** — кастомные выпадения с 30+ типов мобов
- **Сумка артефактов** — портативное хранилище артефактов с автоматическим применением эффектов
- **Ресурс-пак** — автоматически генерируемый ресурс-пак с кастомными моделями и текстурами
- **Многоязычность** — 5 встроенных языковых файлов (EN, RU, DE, FR, ZH)
- **Developer API** — публичное API для регистрации кастомных артефактов, компонентов и триггеров
- **Система фич** — 12 модулей, каждый включается/выключается в config.yml
- **Коллекция артефактов** — `/artifact collection`, отслеживание в БД
- **Сеты артефактов** — бонусы за набор (features/sets.yml)
- **Активные способности** — 6 типов (PROJECTILE, TELEPORT, DASH, SHIELD, HEAL, AOE, COMMAND)
- **Улучшение артефактов** — комбинированием копий
- **Рыбалка** — кастомный лут
- **Элитные мобы** — с множителями и особым дропом
- **XP артефактов** — уровни и масштабирование
- **Арена боссов** — волновая арена
- **AdminItemsGUI** — `/waadmin gui` для выдачи любых предметов плагина
- **Интеграции**: ItemsAdder, Nexo, Oraxen, MythicMobs, PlaceholderAPI — все через рефлексию

---

## 2. Установка

### Требования

- Java 21 или выше
- Paper 1.21.1 или выше (протестировано на 1.21.11)

### Опциональные зависимости

- **WorldEdit** или **FastAsyncWorldEdit** — для вставки схем
- **ItemsAdder**, **Nexo** или **Oraxen** — для использования кастомных предметов в рецептах и луте
- **MythicMobs** — для интеграции с элитными мобами
- **PlaceholderAPI** — для плейсхолдеров

### Шаги установки

1. Скачайте последний `WastelandArtifacts.jar` со страницы релизов
2. Поместите JAR в папку `plugins/` вашего сервера
3. Перезапустите сервер
4. Все файлы конфигурации будут автоматически созданы при первом запуске:
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

### Поведение при первом запуске

- Сканирует все миры на предмет подземелий (если `scanOnStartup: true`)
- Регистрирует все рецепты крафта чертежей
- Запускает HTTP-сервер ресурс-пака (порт 8192 по умолчанию)
- Генерирует JSON-файлы кастомных моделей для всех артефактов
- Инициализирует базу данных (SQLite/MySQL)
- Загружает модули фич в соответствии с config.yml

---

## 3. Команды

### 3.1 `/artifact` — Управление артефактами

**Алиасы:** `/art`, `/wa`

| Подкоманда | Право | Аргументы | Описание |
|---|---|---|---|
| `give` | `wastelandartifacts.admin.blueprint` | `<id> [игрок] [количество]` | Выдать артефакт игроку |
| `list` | `wastelandartifacts.player.artifact` | `[страница]` | Список всех зарегистрированных артефактов |
| `info` | `wastelandartifacts.player.artifact` | `<id>` | Показать подробную информацию об артефакте |
| `reload` | `wastelandartifacts.admin` | — | Перезагрузить все файлы конфигурации |
| `create` | `wastelandartifacts.admin` | — | Открыть внутриигровой редактор артефактов |
| `edit` | `wastelandartifacts.admin` | `<id>` | Редактировать существующий артефакт в GUI |
| `collection` | `wastelandartifacts.player.collection` | — | Показать прогресс коллекции артефактов |

**Примеры:**
```
/artifact give fire_sword PlayerName 1
/artifact list 1
/artifact info necronomicon
/artifact reload
/artifact create
/artifact edit fire_sword
/artifact collection
```

### 3.2 `/waadmin` — Админские команды

**Алиасы:** `/waa`

| Подкоманда | Право | Аргументы | Описание |
|---|---|---|---|
| `gui` | `wastelandartifacts.admin.gui` | — | Открыть GUI выдачи всех предметов (артефакты, кастомные предметы, чертежи) |
| `rp build` | `wastelandartifacts.admin.rp` | — | Собрать ZIP-архив ресурс-пака |
| `rp send` | `wastelandartifacts.admin.rp` | — | Отправить ресурс-пак всем игрокам онлайн |
| `blueprint` | `wastelandartifacts.admin.blueprint` | `<recipe_id>` | Выдать предмет-чертёж |
| `customitem` | `wastelandartifacts.admin.customitem` | `<id> [количество]` | Выдать кастомный предмет-ингредиент |
| `debug` | `wastelandartifacts.admin.debug` | — | Показать отладочную информацию (количество артефактов, компонентов, подземелий, схем, статус ресурс-пака) |

**Примеры:**
```
/waadmin gui
/waadmin rp build
/waadmin rp send
/waadmin blueprint basic_altar_craft_fire_sword
/waadmin customitem fire_core 16
/waadmin debug
```

### 3.3 `/artifact` — Управление артефактами (продолжение)

Команда `/artifact collection` открывает GUI коллекции артефактов, показывая какие артефакты игрок уже нашёл, а какие ещё предстоит найти.

### 3.4 `/altar` — Система алтарей

**Алиасы:** `/alt`

| Подкоманда | Право | Аргументы | Описание |
|---|---|---|---|
| `list` | `wastelandartifacts.player.altar` | — | Список всех уровней алтарей |
| `info` | `wastelandartifacts.player.altar` | `<уровень>` | Показать информацию об алтаре и рецепты |
| `preview` | `wastelandartifacts.player.altar` | `<уровень> [вариант]` | Показать голограмму-превью структуры алтаря |
| `preview stop` | `wastelandartifacts.player.altar` | — | Остановить голограмму-превью |
| `build` | `wastelandartifacts.admin.altar` | `<уровень> [вариант]` | Авто-построить алтарь (расходует блоки из инвентаря) |
| `schematic save` | `wastelandartifacts.admin.altar` | `<имя>` | Сохранить текущий алтарь как схему |
| `schematic paste` | `wastelandartifacts.admin.altar` | `<имя>` | Вставить схему алтаря |
| `schematic list` | `wastelandartifacts.admin.altar` | — | Список сохранённых схем алтарей |

**Примеры:**
```
/altar list
/altar info basic_altar
/altar preview basic_altar
/altar preview stop
/altar build basic_altar
/altar schematic save my_altar
/altar schematic paste my_altar
```

### 3.5 `/bag` — Сумка артефактов

**Алиасы:** `/artifacts`, `/artbag`

**Право:** `wastelandartifacts.player.bag`

**Использование:** `/bag`

Открывает GUI сумки артефактов. Требует наличия артефакта **Wasteland Bag** в инвентаре. Внутрь можно помещать только артефакты. Эффекты от артефактов в сумке с компонентом `POTION_EFFECT_ON_EQUIP` автоматически применяются к игроку.

### 3.6 `/dungeon` — Управление подземельями

**Алиасы:** `/dg`

**Право:** `wastelandartifacts.admin`

| Подкоманда | Описание |
|---|---|
| `scan [мир]` | Сканировать мир(ы) на предмет структур и сопоставить их с конфигами подземелий |
| `paste <схема>` | Вставить схему в местоположении игрока |
| `loot` | Открыть GUI добычи подземелий (просмотр настроенного лута для каждого подземелья) |
| `info` | Показать статистику подземелий (количество конфигов, количество схем) |

**Примеры:**
```
/dungeon scan world
/dungeon paste my_dungeon
/dungeon loot
/dungeon info
```

### 3.7 `/item` — Кодирование/декодирование предметов

**Право:** Требуется базовое право

| Подкоманда | Описание |
|---|---|
| `encode` | Получить Mojang Base64 NBT-строку предмета в руке |
| `decode <base64>` | Создать предмет из Base64 NBT-строки |

**Примеры:**
```
/item encode
/item decode H4sI...
```

### 3.8 `/arena` — Арена боссов

**Алиасы:** `/bossarena`

**Право:** `wastelandartifacts.player.arena`

| Подкоманда | Описание |
|---|---|
| *(без аргументов)* | Открыть GUI арены боссов |
| `start` | Начать волну на арене |

**Примеры:**
```
/arena
/arena start
```

---

## 4. Права

| Узел права | По умолчанию | Родитель | Описание |
|---|---|---|---|
| `wastelandartifacts.*` | op | `admin` + `player` | Все права |
| `wastelandartifacts.admin` | op | `admin.altar`, `admin.blueprint`, `admin.customitem`, `admin.debug`, `admin.rp`, `admin.gui` | Все админские команды |
| `wastelandartifacts.player` | true | `player.altar`, `player.bag`, `player.artifact`, `player.blueprint`, `player.collection`, `player.arena` | Все игровые команды |
| `wastelandartifacts.admin.altar` | op | — | Управление алтарями (постройка, схемы) |
| `wastelandartifacts.admin.blueprint` | op | — | Выдача чертежей |
| `wastelandartifacts.admin.customitem` | op | — | Выдача кастомных предметов |
| `wastelandartifacts.admin.debug` | op | — | Отладочные команды |
| `wastelandartifacts.admin.rp` | op | — | Управление ресурс-паком |
| `wastelandartifacts.admin.gui` | op | — | GUI выдачи предметов |
| `wastelandartifacts.player.altar` | true | — | Просмотр и превью алтарей |
| `wastelandartifacts.player.bag` | true | — | Использование сумки артефактов |
| `wastelandartifacts.player.artifact` | true | — | Список и просмотр информации об артефактах |
| `wastelandartifacts.player.blueprint` | true | — | Использование чертежей на алтарях |
| `wastelandartifacts.player.collection` | true | — | Просмотр коллекции артефактов |
| `wastelandartifacts.player.arena` | true | — | Доступ к арене боссов |

---

## 5. Файлы конфигурации

### 5.1 config.yml

Главный файл конфигурации, расположенный по пути `plugins/WastelandArtifacts/config.yml`.

```yaml
# ─── Wasteland Artifacts — Главный конфиг ───

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
  prompt: "<gold>Этот сервер использует кастомный ресурс-пак!"
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
  title: "<dark_gray>Артефакты Пустоши"
  fill-glass: true

lang:
  locale: ru_RU
```

#### Секция Features

Управляет включением/отключением модулей плагина. Позволяет гибко настроить функционал под нужды сервера:

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `placeholderAPI` | Boolean | `true` | Включить интеграцию с PlaceholderAPI |
| `mythicmobs` | Boolean | `true` | Включить интеграцию с MythicMobs |
| `nexo` | Boolean | `true` | Включить интеграцию с Nexo |
| `oraxen` | Boolean | `true` | Включить интеграцию с Oraxen |
| `collection` | Boolean | `true` | Включить систему коллекции артефактов |
| `artifactSets` | Boolean | `true` | Включить сеты артефактов с бонусами |
| `activeAbilities` | Boolean | `true` | Включить активные способности |
| `upgrades` | Boolean | `true` | Включить улучшение артефактов |
| `fishing` | Boolean | `true` | Включить кастомный лут при рыбалке |
| `customMobs` | Boolean | `true` | Включить элитных мобов |
| `artifactXP` | Boolean | `true` | Включить XP артефактов |
| `bossArena` | Boolean | `true` | Включить арену боссов |

#### Секция Resource Pack

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `mode` | String | `AUTO` | Режим ресурс-пака (AUTO или MANUAL) |
| `autoHost` | Boolean | `true` | Запустить встроенный HTTP-сервер для хостинга |
| `hostPort` | Integer | `8192` | Порт для HTTP-сервера |
| `force` | Boolean | `true` | Принудительно принять ресурс-пак игрокам |
| `prompt` | String | `"..."` | Текст сообщения, показываемого игрокам |
| `hash` | String | `""` | SHA-1 хеш (авто-вычисляется, если пусто) |

#### Секция Dungeons

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `enabled` | Boolean | `true` | Включить систему подземельной добычи |
| `scanOnStartup` | Boolean | `true` | Сканировать все миры на предмет структур при запуске |
| `lootInjection` | Boolean | `true` | Внедрять кастомный лут в сундуки структур |
| `specialChests` | Boolean | `true` | Включить специальную обработку сундуков |
| `bossSpawners` | Boolean | `true` | Включить спавн боссов при открытии сундуков |

#### Секция Crafting

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `enabled` | Boolean | `true` | Включить систему крафта |
| `altarEnabled` | Boolean | `true` | Включить крафт на алтарях |
| `keepIngredients` | Boolean | `false` | Сохранять ингредиенты после крафта (креативный режим) |

#### Секция Artifacts

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `defaultAmount` | Integer | `1` | Количество по умолчанию при выдаче артефактов |
| `dropOnDeath` | Boolean | `false` | Выбрасывать артефакты при смерти игрока |
| `soulbind` | Boolean | `false` | Привязывать артефакты к игроку |
| `allowInAnvil` | Boolean | `false` | Разрешить артефакты в наковальне |

#### Секция Database

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `enabled` | Boolean | `true` | Включить хранение в базе данных |
| `type` | String | `SQLITE` | Тип базы данных (SQLITE или MYSQL) |
| `host` | String | `localhost` | Хост MySQL |
| `port` | Integer | `3306` | Порт MySQL |
| `database` | String | `wasteland_artifacts` | Имя базы данных |
| `user` | String | `root` | Пользователь MySQL |
| `password` | String | `""` | Пароль MySQL |
| `poolSize` | Integer | `10` | Размер пула подключений HikariCP |

#### Секция GUI

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `rows` | Integer | `6` | Количество строк в GUI артефактов (макс. 6) |
| `title` | String | `"..."` | Заголовок GUI (формат MiniMessage) |
| `fill-glass` | Boolean | `true` | Заполнять пустые слоты стеклянными панелями |

#### Секция Lang

| Настройка | Тип | По умолчанию | Описание |
|---|---|---|---|
| `locale` | String | `ru_RU` | Язык по умолчанию (`en_US`, `ru_RU`, `de_DE`, `fr_FR`, `zh_CN`) |

---

### 5.2 custom_items.yml

Расположен по пути `plugins/WastelandArtifacts/custom_items.yml`. Содержит **86 кастомных предметов**, используемых в качестве ингредиентов для крафта артефактов. Каждый предмет имеет уникальное значение `customModelData` (6001–6086).

#### Формат

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

| Поле | Тип | Описание |
|---|---|---|
| `item_id` | String | Уникальный идентификатор, используемый в рецептах и таблицах лута |
| `material` | Material | Ванильный Minecraft материал |
| `name` | String | Отображаемое имя (формат MiniMessage) |
| `lore` | List | Строки описания (формат MiniMessage) |
| `customModelData` | Integer | Значение кастомной модели данных |
| `rarity` | String | Редкость из rarities.yml |

#### Полный список предметов

**Элементы (8):**
- `fire_core` (BLAZE_POWDER, CMD 6001, UNCOMMON)
- `shadow_essence` (INK_SAC, CMD 6002, RARE)
- `frozen_shard` (ICE, CMD 6003, UNCOMMON)
- `storm_crystal` (AMETHYST_SHARD, CMD 6004, EPIC)
- `earth_core` (COBBLESTONE, CMD 6016, UNCOMMON)
- `wind_essence` (FEATHER, CMD 6017, UNCOMMON)
- `water_pearl` (PRISMARINE_SHARD, CMD 6018, RARE)
- `light_fragment` (GLOWSTONE_DUST, CMD 6019, RARE)

**Части существ (8):**
- `warden_heart` (SCULK, CMD 6005, LEGENDARY)
- `ancient_bone` (BONE, CMD 6007, UNCOMMON)
- `dragon_scales` (PRISMARINE_CRYSTALS, CMD 6009, LEGENDARY)
- `wither_skull` (WITHER_SKELETON_SKULL, CMD 6013, LEGENDARY)
- `eye_of_abyss` (ENDER_EYE, CMD 6020, EPIC)
- `shadow_claw` (BLACK_DYE, CMD 6021, RARE)
- `phantom_wing` (PHANTOM_MEMBRANE, CMD 6022, RARE)
- `horn_of_dread` (BONE, CMD 6023, EPIC)

**Древние артефакты (9):**
- `totem_fragment` (TOTEM_OF_UNDYING, CMD 6014, EPIC)
- `ancient_coin` (GOLD_NUGGET, CMD 6024, UNCOMMON)
- `runic_tablet` (STONE, CMD 6025, RARE)
- `elder_shard` (PRISMARINE_SHARD, CMD 6026, EPIC)
- `forgotten_key` (TRIPWIRE_HOOK, CMD 6027, RARE)
- `void_seal` (OBSIDIAN, CMD 6028, EPIC)
- `ethereal_orb` (GLOWSTONE, CMD 6029, LEGENDARY)
- `ancient_crown` (GOLD_INGOT, CMD 6030, LEGENDARY)
- `ring_of_pact` (IRON_NUGGET, CMD 6031, RARE)

**Алхимия (9):**
- `soul_fragment` (GHAST_TEAR, CMD 6008, RARE)
- `arcane_essence` (EXPERIENCE_BOTTLE, CMD 6032, RARE)
- `spectral_dust` (GUNPOWDER, CMD 6033, UNCOMMON)
- `chaos_crystal` (AMETHYST_SHARD, CMD 6034, EPIC)
- `alchemic_potion` (POTION, CMD 6035, RARE)
- `primal_catalyst` (BLAZE_ROD, CMD 6036, EPIC)
- `life_elixir` (HONEY_BOTTLE, CMD 6037, LEGENDARY)
- `purified_salt` (SUGAR, CMD 6038, UNCOMMON)
- `quicksilver` (IRON_NUGGET, CMD 6039, RARE)

**Пустошь (8):**
- `wasteland_sand` (SAND, CMD 6040, COMMON)
- `withered_bone` (BONE, CMD 6041, COMMON)
- `thorn_vine` (VINE, CMD 6042, UNCOMMON)
- `rusted_ingot` (IRON_INGOT, CMD 6043, UNCOMMON)
- `ash_pile` (GUNPOWDER, CMD 6044, COMMON)
- `toxic_slime` (SLIME_BALL, CMD 6045, UNCOMMON)
- `fungal_spore` (RED_MUSHROOM, CMD 6046, RARE)
- `poison_gland` (SPIDER_EYE, CMD 6047, RARE)

**Космос (9):**
- `void_crystal` (ECHO_SHARD, CMD 6006, EPIC)
- `ender_gem` (ENDER_EYE, CMD 6015, RARE)
- `celestial_star` (NETHER_STAR, CMD 6048, LEGENDARY)
- `moon_shard` (QUARTZ, CMD 6049, RARE)
- `sun_fragment` (GLOWSTONE, CMD 6050, EPIC)
- `void_essence` (BLACK_DYE, CMD 6051, EPIC)
- `galaxy_dust` (GLOW_BERRIES, CMD 6052, RARE)
- `nebula_crystal` (AMETHYST_SHARD, CMD 6053, EPIC)
- `meteor_iron` (IRON_INGOT, CMD 6054, RARE)

**Материалы (9):**
- `corrupted_ingot` (NETHERITE_SCRAP, CMD 6010, EPIC)
- `phantom_weave` (PHANTOM_MEMBRANE, CMD 6011, RARE)
- `enchanted_cloth` (WHITE_WOOL, CMD 6055, UNCOMMON)
- `ancient_leather` (LEATHER, CMD 6056, UNCOMMON)
- `spectral_thread` (STRING, CMD 6057, RARE)
- `reinforced_plate` (IRON_INGOT, CMD 6058, UNCOMMON)
- `clockwork_gear` (IRON_NUGGET, CMD 6059, RARE)
- `energy_spring` (COPPER_INGOT, CMD 6060, UNCOMMON)
- `magnetic_core` (IRON_BLOCK, CMD 6061, RARE)

**Мифические (8):**
- `magma_core` (MAGMA_BLOCK, CMD 6012, UNCOMMON)
- `phoenix_feather` (FEATHER, CMD 6062, LEGENDARY)
- `unicorn_horn` (BONE, CMD 6063, LEGENDARY)
- `titan_heart` (NETHERITE_SCRAP, CMD 6064, LEGENDARY)
- `siren_tear` (HEART_OF_THE_SEA, CMD 6065, EPIC)
- `chimera_claw` (FLINT, CMD 6066, EPIC)
- `hydra_tooth` (BONE, CMD 6067, EPIC)
- `kraken_tentacle` (PRISMARINE_CRYSTALS, CMD 6068, LEGENDARY)

**Особые (8):**
- `poseidon_flame` (BLAZE_POWDER, CMD 6069, LEGENDARY)
- `heart_of_poseidon` (HEART_OF_THE_SEA, CMD 6070, VOID)
- `ancient_scrap` (NETHERITE_SCRAP, CMD 6071, RARE)
- `golden_feather` (FEATHER, CMD 6072, RARE)
- `spider_fang` (BONE, CMD 6073, UNCOMMON)
- `echo_shard` (ECHO_SHARD, CMD 6074, RARE)
- `prismarine_core` (PRISMARINE_CRYSTALS, CMD 6075, UNCOMMON)
- `obsidian_shard` (OBSIDIAN, CMD 6076, UNCOMMON)

**Добавленные (v2.0.0) (10):**
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

> **Примечание:** Вы можете добавлять собственные кастомные предметы, следуя тому же формату. Предметы указываются в рецептах и таблицах лута с префиксом `custom:item_id` (например, `custom:fire_core`).

---

### 5.3 artifacts/examples.yml

Расположен по пути `plugins/WastelandArtifacts/artifacts/examples.yml`. Содержит **30+ полностью рабочих примеров артефактов**. Вы можете добавлять собственные файлы артефактов в директорию `artifacts/`, и они будут автоматически загружены.

#### Формат YAML артефакта

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

| Поле | Тип | Обязательно | Описание |
|---|---|---|---|
| `id` | String | Да | Уникальный идентификатор артефакта |
| `displayName` | String | Да | Отображаемое имя (MiniMessage) |
| `lore` | List | Нет | Строки описания (MiniMessage) |
| `baseItem` | Material | Да | Базовый ванильный материал |
| `customModelData` | Float | Нет | Значение кастомной модели данных |
| `rarity` | String | Да | Редкость из rarities.yml |
| `unbreakable` | Boolean | Нет | Делает предмет неломким |
| `maxStackSize` | Integer | Нет | Максимальный размер стака (по умолчанию: стандарт материала) |
| `skinTexture` | String | Нет | Base64 текстура скина для предметов PLAYER_HEAD |
| `components` | List | Да | Список компонентов артефакта |
| `recipe` | Object | Нет | Рецепт крафта (shaped или shapeless) |

#### Типы ингредиентов в рецептах

Рецепты поддерживают 4 формата ингредиентов:

```yaml
# 1) Простой материал по имени:
A: DIAMOND

# 2) Ссылка на другой артефакт:
B: "artifact:fire_sword"

# 3) Ссылка на кастомный предмет из custom_items.yml:
C: "custom:fire_core"

# 4) Расширенный формат с сопоставлением по имени/описанию/CMD (поддерживает跨-плагин предметы):
D:
  type: DIAMOND_SWORD
  name: "<gold>Ancient Blade"
  lore:
    - "<gray>An ancient weapon"
  customModelData: 5010
```

#### Система компонентов

Каждый компонент добавляет артефакту определённую способность. Ниже представлен полный список **17 типов компонентов**:

##### DAMAGE
Добавляет модификатор урона атаки.
```yaml
- type: DAMAGE
  damage: 12.0
```

##### FIRE_ASPECT
Добавляет чары «Заговор огня».
```yaml
- type: FIRE_ASPECT
  level: 2
```

##### ATTRIBUTE
Добавляет модификатор общего атрибута.
```yaml
- type: ATTRIBUTE
  attribute: GENERIC_ARMOR
  amount: 3.0
  operation: ADD_NUMBER        # ADD_NUMBER | MULTIPLY_SCALAR_1 | ADD_SCALAR
```

**Поддерживаемые атрибуты:** GENERIC_ARMOR, GENERIC_ARMOR_TOUGHNESS, GENERIC_ATTACK_DAMAGE, GENERIC_MAX_HEALTH, GENERIC_MOVEMENT_SPEED, GENERIC_KNOCKBACK_RESISTANCE, GENERIC_SAFE_FALL_DISTANCE, GENERIC_JUMP_STRENGTH

**Операции:** `ADD_NUMBER` (плоское сложение), `MULTIPLY_SCALAR_1` (процентный множитель), `ADD_SCALAR`

##### POTION_EFFECT_ON_EQUIP
Применяет эффект зелья, пока артефакт экипирован (в слоте брони, в левой руке или в сумке).
```yaml
- type: POTION_EFFECT_ON_EQUIP
  effect: RESISTANCE
  amplifier: 1
  ambient: true                  # Показывает уменьшенный эффект частиц
```

##### POTION_EFFECT_ON_HIT
Применяет эффект зелья к цели при ударе.
```yaml
- type: POTION_EFFECT_ON_HIT
  effect: POISON
  duration: 100                  # В тиках (5 секунд = 100)
  amplifier: 1
```

##### PARTICLE_ON_HIT
Спавнит частицы при попадании по цели.
```yaml
- type: PARTICLE_ON_HIT
  particle: FLAME
  count: 15
```

##### PARTICLE_AMBIENT
Спавнит фоновые частицы, пока артефакт экипирован.
```yaml
- type: PARTICLE_AMBIENT
  particle: SOUL
  count: 5
```

##### SOUND_ON_HIT
Воспроизводит звук при попадании по существу.
```yaml
- type: SOUND_ON_HIT
  sound: ENTITY_BLAZE_HURT
```

##### SOUND_ON_USE
Воспроизводит звук при использовании артефакта (ПКМ).
```yaml
- type: SOUND_ON_USE
  sound: ENTITY_ILLUSIONER_CAST_SPELL
  volume: 1.0
  pitch: 0.8
```

##### COOLDOWN
Добавляет перезарядку после использования артефакта.
```yaml
- type: COOLDOWN
  seconds: 3
```

##### LIFE_STEAL
Крадёт здоровье у цели при ударе.
```yaml
- type: LIFE_STEAL
  percentage: 0.15               # 15% от нанесённого урона
```

##### LIGHTNING
Призывает молнию в цель при ударе.
```yaml
- type: LIGHTNING
  chance: 0.15                   # 15% шанс
  safe: true                     # Не наносит урон атакующему
```

##### EXPLOSION
Создаёт взрыв при использовании.
```yaml
- type: EXPLOSION
  power: 3.0
  safe: true                     # Не наносит урон игроку
```

##### SUMMON
Призывает мобов при использовании.
```yaml
- type: SUMMON
  entityType: SKELETON
  amount: 3
  duration: 300                  # Тики (15 секунд)
  withEquipment: true            # Спавнит с экипировкой
```

##### PROJECTILE
Стреляет снарядом при использовании.
```yaml
- type: PROJECTILE
  projectileType: FIREBALL       # FIREBALL, WITHER_SKULL и т.д.
  speed: 2.0
```

##### AOE
Создаёт область действия вокруг игрока.
```yaml
- type: AOE
  radius: 8.0
  damage: 10.0                   # Используйте отрицательное для лечения
  effect: SLOWNESS               # Эффект зелья для применения (опционально)
  amplifier: 2                   # Усилитель эффекта (опционально)
  duration: 100                  # Длительность эффекта в тиках (опционально)
```

##### CHARGE
Добавляет ограниченные заряды артефакту.
```yaml
- type: CHARGE
  maxCharges: 5
  consumeOnUse: true             # Расходовать один заряд за использование
  destroyWhenEmpty: false        # Уничтожить предмет при отсутствии зарядов
```

##### COMMAND
Выполняет команду при использовании.
```yaml
- type: COMMAND
  command: "bag"                 # Команда для выполнения (без /)
  asPlayer: true                 # Выполнить как игрок (а не консоль)
```

#### Типы триггеров

Система триггеров позволяет компонентам реагировать на определённые события. Компоненты артефактов можно настроить на срабатывание по определённым триггерам:

| TriggerType | Описание |
|---|---|
| `ON_ATTACK` | Когда игрок атакует существо |
| `ON_HIT` | Когда по игроку попадают |
| `ON_USE` | Когда игрок нажимает ПКМ с артефактом |
| `ON_EQUIP` | Когда артефакт экипирован |
| `ON_UNEQUIP` | Когда артефакт снят |
| `ON_PICKUP` | Когда артефакт подобран |
| `ON_SNEAK` | Когда игрок крадётся |
| `ON_SWING` | Когда игрок замахивается рукой |
| `ON_PROJECTILE_HIT` | Когда снаряд попадает |
| `ON_KILL` | Когда игрок убивает существо |
| `ON_DEATH` | Когда игрок умирает |
| `ON_SPRINT` | Когда игрок бежит |
| `ON_TIMER` | Периодический триггер по таймеру |
| `ON_DAMAGE_TAKEN` | Когда игрок получает урон |

#### Формат рецепта

##### Shaped Recipe (С формой)
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

##### Shapeless Recipe (Бесформенный)
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

Расположен по пути `plugins/WastelandArtifacts/dungeons/default.yml`. Содержит конфигурации для **20 типов структур** — как ванильных, так и кастомных схем.

#### Настроенные типы структур

| ID структуры | Ванильная структура | Режим лута |
|---|---|---|
| `stronghold` | Крепость | INJECT |
| `fortress` | Незерская крепость | INJECT |
| `pillager_outpost` | Аванпост разбойников | INJECT |
| `ruined_portal` | Разрушенный портал | INJECT |
| `buried_treasure` | Закопанные сокровища | INJECT |
| `mineshaft` | Шахта | INJECT |
| `ocean_ruins` | Океанские руины | INJECT |
| `trail_ruins` | Тропные руины | INJECT |
| `trial_chambers` | Испытательные палаты | INJECT |
| `ancient_city` | Древний город | REPLACE |
| `bastion` | Бастионный остаток | INJECT |
| `mansion` | Лесной особняк | INJECT |
| `end_city` | Город Края | INJECT |
| `custom_dungeon_example` | Кастомная схема | INJECT |
| `village` | Деревня | INJECT |
| `temple` | Пустынный храм | INJECT |
| `jungle_temple` | Храм в джунглях | INJECT |
| `igloo` | Иглу | INJECT |
| `shipwreck` | Кораблекрушение | INJECT |
| `monument` | Океанский монумент | INJECT |

#### Формат конфига подземелья

```yaml
dungeons:
  stronghold:
    enabled: true
    loot:
      mode: INJECT                      # INJECT или REPLACE
      replaceChance: 0.15               # 15% шанс (только для режима REPLACE)
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
              duration: -1             # -1 = бесконечно
```

#### Режимы лута

| Режим | Описание |
|---|---|
| `INJECT` | Добавляет кастомный лут вместе с существующим ванильным лутом |
| `REPLACE` | Заменяет часть существующего лута на основе `replaceChance` |

#### Поля конфига босса

| Поле | Тип | Описание |
|---|---|---|
| `entity` | String | Имя типа существа (например, WITHER_SKELETON) |
| `name` | String | Кастомное имя босса (MiniMessage) |
| `artifact` | String | ID артефакта для выпадения |
| `blueprint` | String | ID рецепта чертежа для выпадения |
| `dropChance` | Double | Шанс выпадения предметов (0.0–1.0) |
| `health` | Double | Кастомное здоровье босса |
| `equipment` | Map | Слоты экипировки (mainhand, offhand, helmet, chestplate, leggings, boots) |
| `potionEffects` | List | Постоянные эффекты зелий |

---

### 5.5 mob_loot.yml

Расположен по пути `plugins/WastelandArtifacts/mob_loot.yml`. Настраивает кастомные выпадения для **30+ типов мобов**.

#### Формат

```yaml
mobs:
  BLAZE:
    enabled: true
    drops:
      - item: magma_core
        minAmount: 1
        maxAmount: 2
        chance: 0.7                # 70% шанс
      - item: fire_core
        minAmount: 1
        maxAmount: 2
        chance: 0.4
```

#### Методы сопоставления мобов

Плагин поддерживает 3 способа сопоставления мобов:

##### 1. По EntityType (для ванильных мобов)
```yaml
BLAZE:
  drops:
    - item: magma_core
      chance: 0.7
```

##### 2. По кастомному имени (для MythicMobs, EliteMobs и т.д.)
```yaml
my_boss:
  displayName: "§cHell King"       # Частичное совпадение по кастомному имени
  drops:
    - item: poseidon_flame
      chance: 0.8
```

##### 3. По тегам Scoreboard
```yaml
mythic_boss:
  tags:
    - "mythicmobs:boss"
  drops:
    - item: warden_heart
      chance: 0.5
```

#### Настроенные мобы

BLAZE, WITHER_SKELETON, SKELETON, ZOMBIE, CREEPER, SPIDER, ENDERMAN, MAGMA_CUBE, GHAST, PHANTOM, WARDEN, ELDER_GUARDIAN, WITCH, HOGLIN, PIGLIN_BRUTE, ENDER_DRAGON, RAVAGER, EVOKER, VEX, PILLAGER, VINDICATOR, ZOMBIFIED_PIGLIN, BEE, DROWNED, STRIDER, WOLF

---

### 5.6 altars.yml

Расположен по пути `plugins/WastelandArtifacts/altars.yml`. Определяет **3 уровня алтарей** с 3D-мультиблочными структурами и рецептами.

#### Формат структуры алтаря

Алтари используют послойную 3D-сетку:

```yaml
structures:
  - layers:
      - "OOO"         # Y=0: пол
      - "OAO"         # A = активаторный блок
      - "OOO"
      - "B B"         # Y=1: колонны/арки
      - "   "
      - "B B"
    mapping:
      O: OBSIDIAN
      A: CHISELED_STONE_BRICKS
      B: POLISHED_BLACKSTONE_BRICKS
```

Каждый символ в строке слоя сопоставляется с материалом блока через секцию `mapping`. Пробелы обозначают воздух.

#### Уровни алтарей

##### Базовый алтарь (Уровень 1)
- **Активатор:** CHISELED_STONE_BRICKS
- **Структура:** Основа 3×3 с арками
- **Глобальная перезарядка:** 5 секунд
- **Рецепты:** Fire Sword, Shadow Cloak

##### Продвинутый алтарь (Уровень 2)
- **Активатор:** RESPAWN_ANCHOR
- **Структура:** Основа 5×5 со стенами и колоннами
- **Глобальная перезарядка:** 10 секунд
- **Рецепты:** Ice Breaker, Necronomicon

##### Легендарный алтарь (Уровень 3)
- **Активатор:** RESPAWN_ANCHOR
- **Структура:** Основа 5×5 с незеритовыми блоками
- **Глобальная перезарядка:** 30 секунд
- **Рецепты:** Warden Chestplate, Staff of Storms, Holy Grail, Wasteland Bag

#### Формат рецепта

```yaml
recipes:
  - id: craft_fire_sword                    # ID рецепта (должен быть уникальным)
    result: fire_sword                       # ID артефакта для крафта
    experience: 5                            # Требуемый уровень опыта
    cooldown: 10                             # Перезарядка рецепта в секундах
    catalyst:                                # Опциональный катализатор (расходуется)
      item: NETHER_STAR
      consume: true
    blueprintMaterial: PAPER                 # Материал предмета-чертежа
    blueprintName: "<red>📜 Blueprint: Fire Sword"  # Отображаемое имя чертежа
    blueprintCustomModelData: 5001           # Кастомная модель данных чертежа
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
        name: "<red>Fire Core"              # Сопоставление по имени
        customModelData: 6001               # Сопоставление по CMD
```

#### Секция Settings

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

#### Формат ID рецепта чертежа

Для рецептов алтаря, требующих чертёж, ID рецепта чертежа, используемый в верстаке, следует этому формату:
```
<название_уровня>_<id_рецепта>
```

Пример: `basic_altar_craft_fire_sword`

Этот ID рецепта используется в `blueprint_workbench.yml` и указывается в луте чертежей подземелий.

---

### 5.7 blueprint_workbench.yml

Расположен по пути `plugins/WastelandArtifacts/blueprint_workbench.yml`. Определяет рецепты крафта в верстаке для создания чертежей алтарей.

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

  # Прямой крафт артефакта (создаёт сам артефакт, а не чертёж)
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

#### Справочник полей рецепта

| Поле | Тип | Описание |
|---|---|---|
| `enabled` | Boolean | Включить/отключить рецепт |
| `type` | String | `direct_artifact` или опущено (по умолчанию: blueprint) |
| `result` | String | ID артефакта (для direct_artifact) |
| `pattern` | List | 3-рядный шаблон крафта |
| `ingredients` | Map | Сопоставление символа с ингредиентом |

#### Авто-запасной вариант

Если рецепт не определён в `blueprint_workbench.yml`, плагин автоматически генерирует рецепт верстака на основе уровня алтаря:
- **Уровень 1:** Материалы уровня бронзы/железа
- **Уровень 2:** Материалы уровня алмаза/изумруда
- **Уровень 3:** Материалы уровня незерита/алмаза

#### Порядок разрешения ингредиентов

При разрешении ингредиентов в рецептах верстака плагин проверяет:
1. `artifact:artifact_id` — Точное совпадение предмета артефакта
2. `custom:item_id` — Совпадение из `custom_items.yml`
3. `itemsadder:namespace:id` — Предмет ItemsAdder
4. Обычное имя материала — Ванильный `Material`
5. Расширенный формат (с `type`, `name`, `lore`, `customModelData`) — Совпадение через `ItemStack.isSimilar()`

---

### 5.8 rarities.yml

Расположен по пути `plugins/WastelandArtifacts/rarities.yml`. Определяет **8 встроенных редкостей**, которые определяют цвета имени предмета и оформление.

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

| Поле | Тип | Описание |
|---|---|---|
| `displayName` | String | Отображаемое имя редкости (MiniMessage) |
| `color` | String | HEX-код цвета (например, `#FFAA00`) |
| `decoration` | String | Оформление текста: `BOLD`, `ITALIC`, `STRIKETHROUGH`, `UNDERLINE`, `MAGIC` |
| `order` | Integer | Порядок сортировки (выше = реже/ценнее) |

Вы можете добавлять кастомные редкости, создавая новые записи:

```yaml
CUSTOM_LEGEND:
  displayName: "<gradient:gold:red>Legend"
  color: "#FFD700"
  decoration: BOLD
  order: 8
```

---

### 5.9 balance.yml

Расположен по пути `plugins/WastelandArtifacts/balance.yml`. Контролирует лимиты суммирования эффектов, настройки сумки и поведение в левой руке.

```yaml
stacking:
  maxAmplifier: 4                  # Глобальный макс. усилитель для любого эффекта
  enabled: true
  perEffect:
    SPEED:
      maxAmplifier: 3             # Скорость макс. IV
    STRENGTH:
      maxAmplifier: 2             # Сила макс. III
    REGENERATION:
      maxAmplifier: 1             # Регенерация макс. II
    RESISTANCE:
      maxAmplifier: 2             # Сопротивление макс. III
    JUMP_BOOST:
      maxAmplifier: 3             # Прыгучесть макс. IV
    HEALTH_BOOST:
      maxAmplifier: 4             # Здоровье макс. V
    ABSORPTION:
      maxAmplifier: 4             # Поглощение макс. V

bag:
  maxSlots: 54                    # Макс. количество слотов в сумке (макс. 54)
  allowSameInBag: true            # Разрешить одинаковые артефакты в сумке
  effectApplyDelay: 20            # Тики между пересчётом эффектов

offhand:
  enabled: true                   # Артефакты работают в левой руке
  allowStacking: true             # Суммирование с эффектами из сумки
```

#### Объяснение суммирования эффектов

Когда у игрока есть несколько артефактов с одинаковым эффектом `POTION_EFFECT_ON_EQUIP`, усилители складываются аддитивно. Например:
- Артефакт A: Скорость I (усилитель 0)
- Артефакт B: Скорость II (усилитель 1)
- Результат: Скорость III (усилитель 2)

Настройка `maxAmplifier` ограничивает это суммирование для каждого эффекта. Глобальный `maxAmplifier: 4` действует как абсолютный предел.

---

### 5.10 Языковые файлы

Расположены по пути `plugins/WastelandArtifacts/lang/`. Плагин поставляется с **5 языковыми файлами**:

| Файл | Язык |
|---|---|
| `en_US.yml` | Английский (запасной) |
| `ru_RU.yml` | Русский |
| `de_DE.yml` | Немецкий |
| `fr_FR.yml` | Французский |
| `zh_CN.yml` | Китайский |

#### Как работают переводы

Сообщения хранятся в плоском формате «ключ-значение» с поддержкой плейсхолдеров `String.format()` (`%s`, `%d` и т.д.):

```yaml
prefix: "<gradient:red:gold>Artifacts</gradient> <dark_gray>»</dark_gray>"
no-permission: "<red>No permission!"
artifact.not-found: "<red>Artifact '%s' not found!"
```

Язык игрока определяется автоматически. Если ключ перевода отсутствует в языке игрока, плагин использует запасной `ru_RU.yml`.

#### Добавление нового языка

1. Создайте новый файл в `lang/` (например, `lang/es_ES.yml`)
2. Скопируйте ключи из `ru_RU.yml` и переведите значения
3. Установите `lang.locale: es_ES` в `config.yml`

---

### 5.11 features/*.yml

Расположены по пути `plugins/WastelandArtifacts/features/`. Каждый файл соответствует модулю фич, управляемому через секцию `features` в `config.yml`. Если модуль отключён, его конфиг не загружается.

#### features/collection.yml

Система коллекции артефактов. Отслеживает какие артефакты игрок нашёл и выдаёт награды за прогресс.

```yaml
collection:
  enabled: true
  gui-title: "<dark_gray>📚 Коллекция артефактов"
  rewards:
    - percent: 25
      command: "give %player% diamond 1"
      message: "<gold>🎉 25% коллекции! +1 алмаз"
    - percent: 50
      command: "give %player% netherite_ingot 1"
      message: "<gold>🎉 50% коллекции! +1 незерит"
    - percent: 75
      command: "give %player% enchanted_golden_apple 1"
      message: "<gold>🎉 75% коллекции! +1 зачарованное яблоко"
    - percent: 100
      command: "give %player% nether_star 1"
      message: "<gold>🎉 100% коллекции! +1 звезда Незера"
```

#### features/sets.yml

Сеты артефактов. При надевании нескольких артефактов из одного сета игрок получает бонусы.

```yaml
sets:
  fiery_set:
    enabled: true
    name: "<red>Огненный набор"
    artifacts:
      - "fire_sword"
      - "shadow_cloak"
      - "staff_of_storms"
    bonuses:
      - pieces: 2
        description: "<red>Огненная аура"
        effects:
          - "POTION:FIRE_RESISTANCE:0:999999"
          - "ATTRIBUTE:GENERIC_ATTACK_DAMAGE:2:0"
      - pieces: 3
        description: "<gold>Пылающий гнев"
        effects:
          - "POTION:STRENGTH:1:999999"
          - "POTION:SPEED:1:999999"
          - "ATTRIBUTE:GENERIC_MAX_HEALTH:10:0"
```

#### features/abilities.yml

Активные способности для артефактов. Каждая способность имеет тип, кулдаун, урон, радиус, эффекты и визуальное оформление.

Поддерживаемые типы способностей:

| Тип | Описание |
|---|---|
| `PROJECTILE` | Запускает снаряд (например, файербол) |
| `TELEPORT` | Телепортирует игрока вперёд |
| `DASH` | Рывок в направлении взгляда |
| `SHIELD` | Даёт поглощение и сопротивление |
| `HEAL` | Восстанавливает здоровье |
| `AOE` | Наносит урон и применяет эффекты в области |
| `COMMAND` | Выполняет команду |

```yaml
abilities:
  fireball:
    enabled: true
    name: "<red>Огненный шар"
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
      - "<gray>Запускает огненный шар"
      - "<gray>Урон: <red>8"
      - "<gray>Кулдаун: <white>10с"
```

#### features/upgrades.yml

Улучшение артефактов путём комбинирования нескольких копий одного артефакта.

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
    - "<gray>Уровень: <green>%level%"
    - "<gray>Множитель урона: <gold>x%damage_mult%"
```

#### features/fishing_loot.yml

Кастомный лут при рыбалке. Добавляет шанс выловить предметы плагина вместо обычной рыбы.

```yaml
fishing:
  enabled: true
  entries:
    - item: "water_pearl"
      weight: 10
      minCount: 1
      maxCount: 2
      message: "<aqua>🐟 Вы выловили Водную Жемчужину!"
```

#### features/elites.yml

Элитные мобы — редкие варианты обычных мобов с повышенными характеристиками и особым дропом.

```yaml
elites:
  enabled: true
  spawnChance: 0.1
  types:
    - entity: ZOMBIE
      name: "<red>☠ Элитный Зомби"
      healthMultiplier: 3.0
      damageMultiplier: 2.0
      dropChance: 0.5
      drops:
        - "soul_fragment"
        - "ancient_bone"
```

#### features/xp.yml

Система опыта артефактов. Артефакты получают XP за убийства мобов и повышают свои характеристики с каждым уровнем.

```yaml
xp:
  enabled: true
  xpPerKill: 10
  xpPerLevel: 100
  maxLevel: 50
  damagePerLevel: 0.05
  levelUpSound: ENTITY_PLAYER_LEVELUP
  levelUpMessage: "<green>⬆ Артефакт <gold>%artifact% <green>достиг уровня <yellow>%level%!"
```

#### features/arena.yml

Арена боссов (волновая). Игрок сражается с волнами мобов и боссов.

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

## 6. Интеграции

Wasteland Artifacts интегрируется с другими плагинами через рефлексию, что позволяет избежать жёстких зависимостей. Если плагин не установлен, его функции корректно пропускаются.

### ItemsAdder

Позволяет использовать предметы ItemsAdder в любом месте конфигурации плагина.

**Префикс:** `itemsadder:namespace:id`

```yaml
# В рецептах:
ingredients:
  A: "itemsadder:my_namespace:my_item"

# В луте подземелий:
customItems:
  - id: "itemsadder:my_namespace:my_item"
    weight: 10
    minCount: 1
    maxCount: 3

# В выпадениях с мобов:
drops:
  - item: "itemsadder:my_namespace:my_item"
    chance: 0.5
```

**Поддерживаемые места:**
- Ингредиенты рецептов алтаря
- Ингредиенты рецептов верстака
- Таблицы лута подземелий (секция customItems)
- Выпадения с мобов

### Nexo

Интеграция с Nexo для использования кастомных предметов Nexo. Работает аналогично ItemsAdder через рефлексию.

**Префикс:** `nexo:item_id`

### Oraxen

Интеграция с Oraxen для использования кастомных предметов Oraxen. Работает через рефлексию.

**Префикс:** `oraxen:item_id`

### MythicMobs

Интеграция с MythicMobs. Позволяет сопоставлять элитных мобов по их кастомным именам и тегам, а также использовать MythicMobs в конфигурациях выпадения.

### PlaceholderAPI

Добавляет плейсхолдеры для отображения данных артефактов, коллекции, XP и статистики арены.

**Примеры плейсхолдеров:**
- `%wastelandartifacts_collection_progress%` — прогресс коллекции
- `%wastelandartifacts_artifact_level%` — уровень артефакта в руке
- `%wastelandartifacts_arena_waves%` — пройденные волны на арене

---

## 7. Защита крафта

Все кастомные предметы (артефакты, кастомные ингредиенты) **защищены от ванильного крафта**. `CraftingProtectionListener` предотвращает использование кастомных предметов в:

- **Верстаке** — Кастомные предметы нельзя поместить ни в один рецепт крафта
- **Печи / Плавильной печи / Коптильне** — Нельзя переплавить
- **Наковальне** — Нельзя переименовать или объединить
- **Точиле** — Нельзя снять чары
- **Кузнечном столе** — Нельзя использовать в кузнечных шаблонах

Эта защита применяется к любому предмету с:
- PDC-ключом `wastelandartifacts:artifact_id` (артефакты)
- PDC-ключом `wastelandartifacts:placed_custom_item` (кастомные предметы)
- Предметам, совпадающим с записями в `CustomItemRegistry`

---

## 8. Система подземельной добычи

### Как работает генерация лута

Система подземельной добычи использует несколько хуков для гарантированного появления кастомного лута в сундуках структур:

1. **Обнаружение генерации структуры** (`AsyncStructureGenerateEvent`)
   - Когда генерируется структура, плагин помечает ближайшие сундуки PDC-ключом `wastelandartifacts:dungeon_id`
   - Сопоставляет ключи реестра структур с ID конфигов подземелий

2. **Внедрение таблицы лута** (`LootGenerateEvent`)
   - Перехватывает генерацию ванильной таблицы лута
   - Добавляет артефакты WA, чертежи и кастомные предметы в сгенерированный лут

3. **Хук открытия инвентаря** (`InventoryOpenEvent`)
   - Запасной вариант для сундуков в вагонетках и уже сгенерированных сундуков
   - Проверяет наличие PDC-ключа `wastelandartifacts:loot_populated`
   - Если не заполнен, генерирует и внедряет лут
   - Также обрабатывает **спавн боссов** (20% шанс при первом открытии сундука)

### Обнаружение структур

Плагин сопоставляет ванильные типы структур с конфигами подземелий:

```java
mapStructureToDungeon("stronghold")  → "stronghold"
mapStructureToDungeon("fortress")    → "fortress"
// ... ещё 18 сопоставлений
```

### Однократная генерация

Каждый сундук помечается PDC-ключом `wastelandartifacts:loot_populated` для предотвращения повторной генерации лута. После заполнения сундук больше не будет изменён.

### Спавн боссов

Когда игрок открывает сундук в подземелье:
1. 20% шанс заспавнить босса
2. Босс настраивается в `dungeons/default.yml` в секции `bosses.types`
3. Босс имеет кастомное имя, здоровье, экипировку и эффекты зелий
4. При смерти выпадают артефакты и/или чертежи на основе шансов, сохранённых в PDC

### Кастомные схемы

Чтобы добавить кастомные подземелья:
1. Поместите файлы `.nbt` (Structure Block) или `.schem`/`.schematic` (WorldEdit) в `plugins/WastelandArtifacts/schematics/`
2. Создайте запись конфига подземелья в `dungeons/default.yml` или новом файле в `dungeons/`
3. ID подземелья должен совпадать с именем файла схемы (без расширения)

---

## 9. Размещение кастомных блоков

`CustomItemBlockListener` обрабатывает размещение и разрушение кастомных предметов в виде блоков. Использует комбинацию `ConcurrentHashMap` в памяти и PDC на блоке для надёжного отслеживания всех типов блоков (не только `TileState`).

### Размещение

Когда игрок нажимает ПКМ для размещения кастомного предмета (например, кастомного блока из `custom_items.yml`):
1. Слушатель сохраняет ID предмета в `ConcurrentHashMap<BlockKey, String>` с ключом по координатам блока
2. Если блок поддерживает `TileState`, ID также сохраняется в PDC-ключ `wastelandartifacts:placed_custom_item`
3. Один предмет расходуется из руки игрока

### Разрушение

Когда игрок разрушает размещённый кастомный блок:
1. Слушатель сначала проверяет `HashMap` в памяти для получения ID предмета
2. Если в памяти не найдено, проверяет PDC блоков с `TileState`
3. Если ID найден, выпадает кастомный предмет вместо ванильного блока
4. Кастомный предмет сохраняет свои исходные NBT-данные (имя, описание, CMD)

Двухуровневое хранение (HashMap + PDC) гарантирует корректную работу для всех типов блоков, включая те, которые не имеют `TileState` (например, песок, гравий, обычные блоки).

---

## 10. База данных

Wasteland Artifacts использует HikariCP для подключения к базе данных. Поддерживаются SQLite (по умолчанию) и MySQL.

### Конфигурация

Настройка БД производится в `config.yml` в секции `database`:

```yaml
database:
  enabled: true
  type: SQLITE        # SQLITE или MYSQL
  host: localhost
  port: 3306
  database: wasteland_artifacts
  user: root
  password: ""
  poolSize: 10
```

### Типы БД

| Тип | Файл/Хост | Пул подключений |
|---|---|---|
| `SQLITE` | `plugins/WastelandArtifacts/data.db` | 1 (SQLite не поддерживает конкурентные записи) |
| `MYSQL` | `host:port/database` | Настраивается через `poolSize` (по умолчанию 10) |

### Структура таблиц

Плагин использует 4 таблицы:

#### wa_players

Хранит основную информацию об игроках.

```sql
CREATE TABLE IF NOT EXISTS wa_players (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(16),
    last_seen BIGINT,
    total_found INT DEFAULT 0
);
```

#### wa_artifact_data

Хранит данные об артефактах игроков (уровень, XP, убийства).

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

Отслеживает какие артефакты игрок нашёл.

```sql
CREATE TABLE IF NOT EXISTS wa_collection (
    player_uuid VARCHAR(36),
    artifact_id VARCHAR(64),
    found_date BIGINT,
    PRIMARY KEY (player_uuid, artifact_id)
);
```

#### wa_arena_stats

Статистика игроков на арене боссов.

```sql
CREATE TABLE IF NOT EXISTS wa_arena_stats (
    player_uuid VARCHAR(36) PRIMARY KEY,
    waves_cleared INT DEFAULT 0,
    bosses_killed INT DEFAULT 0,
    best_time BIGINT DEFAULT 0
);
```

### Система фич с поддержкой БД

Некоторые модули требуют наличия базы данных для работы:
- **Коллекция** (`collection`) — сохраняет прогресс игрока
- **Улучшение** (`upgrades`) — хранит уровни артефактов
- **XP артефактов** (`artifactXP`) — сохраняет опыт артефактов
- **Арена боссов** (`bossArena`) — записывает статистику игроков

Если `database.enabled: false`, эти модули автоматически отключаются.

---

## 11. API

### Получение экземпляра API

```java
WastelandArtifactsAPI api = WastelandArtifacts.getInstance().getApi();
```

### Доступные методы

```java
// Регистрация артефактов
void registerArtifact(@NotNull Artifact artifact)
void unregisterArtifact(@NotNull String id)
Artifact getArtifact(@NotNull String id)
List<Artifact> getAllArtifacts()

// Создание предметов
ItemStack createItem(@NotNull Artifact artifact)
ItemStack createItem(@NotNull String artifactId)

// Определение артефакта
Artifact getArtifactFromItem(@NotNull ItemStack item)
boolean isArtifact(@NotNull ItemStack item)

// Выдача артефактов
void giveArtifact(@NotNull Player player, @NotNull String artifactId, int amount)

// Регистрация компонентов и триггеров
void registerComponent(@NotNull String id, @NotNull Class<? extends ArtifactComponent> clazz)
void registerTriggerType(@NotNull TriggerType type, @NotNull Trigger trigger)

// Перезагрузка
void reload()

// Экземпляр плагина
WastelandArtifacts getPlugin()
```

### Пример использования

```java
// Получить API
WastelandArtifactsAPI api = WastelandArtifacts.getInstance().getApi();

// Создать и выдать артефакт
api.giveArtifact(player, "fire_sword", 1);

// Проверить, является ли предмет артефактом
if (api.isArtifact(itemStack)) {
    Artifact artifact = api.getArtifactFromItem(itemStack);
}

// Создать предмет из артефакта
ItemStack item = api.createItem("warden_chestplate");

// Зарегистрировать кастомный артефакт
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

## 12. Решение проблем

### Частые проблемы и решения

#### Ресурс-пак не отправляется игрокам
- Убедитесь, что `autoHost: true` в `config.yml`
- Проверьте, что порт 8192 не заблокирован брандмауэром
- Попробуйте `/waadmin rp build`, затем `/waadmin rp send`

#### Артефакты не имеют кастомной модели/текстуры
- Ресурс-пак должен быть принят игроком
- Выполните `/waadmin rp build` для перегенерации файлов моделей
- Проверьте значения `customModelData` на конфликты с другими плагинами

#### Крафт на алтаре не работает
- Убедитесь, что структура алтаря совпадает в точности (используйте `/altar preview`)
- Проверьте, что у вас есть необходимый чертёж в инвентаре
- Выбрасывайте предметы (клавиша Q) на блоки алтаря, а не нажимайте ПКМ
- Проверьте `globalCooldown` алтаря и `cooldown` рецепта

#### Рецепты чертежей не отображаются
- Убедитесь, что `crafting.enabled: true` в `config.yml`
- Чертежи автоматически обнаруживаются при входе игрока
- Проверьте формат ID рецепта: `<уровень>_<рецепт>`

#### Подземельный лут не внедряется
- Проверьте `dungeons.enabled: true` в `config.yml`
- Выполните `/dungeon scan` для заполнения кэша структур
- Убедитесь, что `lootInjection: true`
- Для существующих миров структуры нужно пересканировать

#### Выпадения с мобов не работают
- Убедитесь, что тип моба написан правильно (имя EntityType)
- Для сопоставления по кастомному имени убедитесь, что `displayName` в конфиге совпадает с кастомным именем моба
- Проверьте, что значения `chance` находятся между 0.0 и 1.0

#### Ошибки плагина при запуске
- Убедитесь, что используется **Java 21** или выше
- Убедитесь, что используется **Paper 1.21.1** или выше (не Spigot/CraftBukkit)
- Проверьте на наличие конфликтующих плагинов, использующих похожие NBT/PDC-ключи

#### База данных не подключается
- Проверьте `database.enabled: true` в `config.yml`
- Для MySQL убедитесь, что хост и порт доступны
- Проверьте логи на наличие SQL ошибок

#### Фичи не загружаются
- Убедитесь, что соответствующие настройки в секции `features` включены
- Некоторые модули (collection, upgrades, xp, arena) требуют включённой БД
- Проверьте, что файлы в `features/` существуют и имеют корректный YAML

### Включение отладочного режима

Используйте `/waadmin debug` для просмотра:
- Количество зарегистрированных артефактов
- Количество зарегистрированных компонентов
- Количество загруженных конфигов подземелий
- Количество загруженных схем
- Статус сервера ресурс-пака

### Файлы логов

Проверяйте `logs/latest.log` на наличие:
- Сообщений инициализации плагина (включая время загрузки в мс)
- Статуса интеграций (ItemsAdder, Nexo, Oraxen, MythicMobs, PlaceholderAPI)
- Прогресса сканирования подземелий
- Статуса генерации ресурс-пака
- Любых ошибок парсинга YAML
- Статуса подключения к базе данных

---

> **Wasteland Artifacts v2.1.0** — Создано animesao для Paper 1.21.11+.
