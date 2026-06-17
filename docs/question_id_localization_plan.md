# Question ID Localization Plan

## Context

There is a current product bug in the question progress flow:

- when the app language changes, previously answered questions can become unanswered again
- after reinstall, answered question state can also be lost

For now, the main pain point is **language switching**. Reinstall is considered a rare event, so we do not need to fully solve reinstall recovery in the first implementation.

## Root Cause

Right now question identity is effectively tied to the localized question text.

Current behavior:

- local Realm question rows store `question`
- answer persistence looks up a question by `question`
- language sync rewrites localized questions in Realm
- after translation changes, the same logical question has a different text
- previous `isAnswered` flags no longer map reliably

This means we are using localized content as identity, which is structurally unsafe.

## Recommended Direction

Introduce a stable `questionId` for every question.

Properties of `questionId`:

- stable across all locales
- independent from display text
- independent from Firestore result ordering
- independent from future text edits

Example format:

- `france_001`
- `france_002`
- `germany_001`
- `champions_014`
- `world_007`
- `vsrm_003`

## Scope Decision

We will implement a **language-safe question identity fix** first.

This first phase should:

- preserve answered state after language switch
- avoid matching answered state by question text
- introduce Realm schema migration for new `questionId` fields

This first phase does **not** need to:

- fully restore exact answered question state after reinstall
- add cloud sync of answered question IDs
- extract question progress into a separate table yet

## Why Realm Migration Is Required

Realm migration is required because question entities will get a new field:

- `questionId`

Current Realm setup:

- `schemaVersion(1)`

After introducing `questionId`, schema must move forward:

- `schemaVersion(2)`

Without a migration, existing installs will crash with a Realm migration requirement on update.

## Migration Strategy

We should use a **simple schema migration**, not a smart recovery migration.

Migration should:

- add `questionId` field to all question Realm models
- move schema from version `1` to `2`

Migration should **not** try to:

- infer `questionId` from old localized text
- rebuild full answered history from old rows
- reconcile old answered rows across languages

This keeps the migration safe and predictable.

## Data Model Changes

### Firestore

Each question document must contain:

- `questionId`
- `translation`
- `place`
- `question`
- `answers`

Important rule:

- translations of the same logical question must share the same `questionId`

### App Models

Add `questionId` to:

- `GameObjectSerializable`
- `QuestionModel`

Optional:

- add it to a shared local contract if that improves consistency

### Realm Question Models

Add `questionId` string field to all question entities:

- `DB_France`
- `DB_Germany`
- `DB_Italy`
- `DB_England`
- `DB_Spain`
- `DB_Super_Cup`
- `DB_Europa_League`
- `DB_Europ_Championship`
- `DB_Champions_League`
- `DB_World_Championship`
- `DB_VS_Ronaldo_Messi`
- `DB_VS_RealM_Barcelona`

Important:

- do **not** switch the primary key in this phase
- keep current Realm row PK strategy as-is

## Logic Changes

### Firestore Mapping

`FirestoreQuestionService` must read `questionId` from Firestore and pass it through app models.

### Local Bootstrap

`DB_Helper.setQuestionsToDB()` must persist `questionId` into local Realm question rows.

### Play Answer Persistence

`RealmPlayProgressLocalDataSource.persistAnswer()` must stop matching by:

- `question`

and instead match by:

- `questionId`

### Language Sync Preservation

`QuestionsRepositoryImpl.ensureLocalizationSync()` must preserve answered state by `questionId`.

Expected flow:

1. read currently answered local question IDs before rewriting localized questions
2. fetch localized questions from Firestore
3. for each fetched question:
   - if its `questionId` was previously answered, set `answered = true`
   - otherwise set `answered = false`
4. write refreshed localized questions back to Realm

This is the core fix for the language-switch issue.

## Recommended Implementation Phases

### Phase 1: Introduce Stable Question Identity

- add `questionId` to Firestore
- add `questionId` to app models
- add `questionId` to Realm question entities
- add Realm migration `1 -> 2`

### Phase 2: Switch Persistence to `questionId`

- pass `questionId` through play session models
- update answer persistence to use `questionId`
- stop using question text as identity in write paths

### Phase 3: Preserve Answered State on Localization Sync

- collect local answered question IDs before question refresh
- restore `answered` flags onto newly localized question rows by `questionId`
- verify language-switch continuity end-to-end

### Optional Phase 4: Future Hardening

Potential later improvements:

- extract question progress into a separate Realm table
- sync answered question IDs to cloud
- support precise answered-question restore after reinstall

These are explicitly outside the first implementation slice.

## Risks

### Risk 1: Firestore Data Inconsistency

If translations of the same logical question do not share the exact same `questionId`, answered state will drift.

Mitigation:

- define a strict naming convention
- review Firestore data before shipping app changes

### Risk 2: Partial Migration Rollout

If app code expects `questionId`, but Firestore data is not fully populated yet, localization sync may degrade.

Mitigation:

- deploy Firestore data updates first
- then release app code

### Risk 3: Legacy Users Won't Get Perfect Historical Recovery

Existing users with old local question rows do not have a trustworthy stable ID history.

Mitigation:

- accept this as a one-time transition cost
- optimize for correctness after rollout, not perfect retroactive repair

## Test Checklist

### Core Functional Tests

- answer several questions in one locale
- change app language
- confirm the same logical questions remain answered

- answer questions in multiple categories
- change language
- confirm category counts and place counts still match expected answered state

- reopen the app after language change
- confirm answered state remains stable

### Regression Tests

- reset place progress and verify question answered states reset correctly
- restore cloud stats and verify app does not crash
- verify play flow still updates category scores and place scores correctly

### Migration Tests

- install old build with schema version `1`
- play and answer questions
- upgrade to new build with schema version `2`
- confirm app launches without Realm migration crash

## Execution Task

### Task Name

Implement stable `questionId` flow to preserve answered state across language changes

### Task Breakdown

1. Prepare Firestore question documents with stable `questionId`
2. Add `questionId` to app transport models
3. Add `questionId` to all Realm question entities
4. Add Realm migration from schema version `1` to `2`
5. Persist `questionId` during local question bootstrap
6. Pass `questionId` through play session flow
7. Persist answered state by `questionId` instead of question text
8. Preserve answered IDs during localization sync
9. Run migration and localization regression testing

### Suggested Commit Split

1. `Add questionId to models and Realm schema`
2. `Persist answered questions by questionId`
3. `Preserve answered state during localization sync`

## Recommendation

This is the best next-step architecture if the priority is:

- fixing language-switch progress loss
- keeping implementation size reasonable
- avoiding a larger progress-table redesign right now

It is a solid middle path:

- not a hack
- not overengineered
- and strong enough to support future evolution later
