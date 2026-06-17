package com.arthuralexandryan.footballquiz.db_app.migration

import io.realm.DynamicRealm

interface RealmMigrationStep {
    val fromVersion: Long
    fun migrate(realm: DynamicRealm)
}
