package com.arthuralexandryan.footballquiz.db_app.migration

import io.realm.DynamicRealm

object Migration1To2 : RealmMigrationStep {
    override val fromVersion: Long = 1L

    override fun migrate(realm: DynamicRealm) {
        val schema = realm.schema

        listOf(
            "DB_France",
            "DB_Germany",
            "DB_Italy",
            "DB_England",
            "DB_Spain",
            "DB_Super_Cup",
            "DB_Europa_League",
            "DB_Europ_Championship",
            "DB_Champions_League",
            "DB_World_Championship",
            "DB_VS_Ronaldo_Messi",
            "DB_VS_RealM_Barcelona"
        ).forEach { modelName ->
            schema.get(modelName)?.let { modelSchema ->
                if (!modelSchema.hasField("questionId")) {
                    modelSchema.addField("questionId", String::class.java)
                }
            }
        }
    }
}
