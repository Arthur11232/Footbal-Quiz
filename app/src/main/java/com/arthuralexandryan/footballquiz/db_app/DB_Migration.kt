package com.arthuralexandryan.footballquiz.db_app

import com.arthuralexandryan.footballquiz.db_app.migration.Migration0To1
import com.arthuralexandryan.footballquiz.db_app.migration.Migration1To2
import com.arthuralexandryan.footballquiz.db_app.migration.RealmMigrationStep
import io.realm.DynamicRealm
import io.realm.RealmMigration

class DB_Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var version = oldVersion

        while (version < newVersion) {
            val step = migrationsByVersion[version]
                ?: error("Missing Realm migration step for version $version")
            step.migrate(realm)
            version++
        }
    }

    companion object {
        const val CURRENT_SCHEMA_VERSION = 2L

        private val migrationsByVersion: Map<Long, RealmMigrationStep> = listOf(
            Migration0To1,
            Migration1To2
        ).associateBy { it.fromVersion }
    }
}
