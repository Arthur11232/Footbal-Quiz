package com.arthuralexandryan.footballquiz.db_app.migration

import io.realm.DynamicRealm
import io.realm.FieldAttribute

object Migration0To1 : RealmMigrationStep {
    override val fromVersion: Long = 0L

    override fun migrate(realm: DynamicRealm) {
        val schema = realm.schema

        schema.get("FQ_Scores")?.let { userSchema ->
            if (userSchema.hasField("versus")) {
                userSchema.renameField("versus", "vsRM")
            }
            if (userSchema.hasField("versus_answered")) {
                userSchema.renameField("versus_answered", "vsRM_answered")
            }
            if (!userSchema.hasField("vsRB")) {
                userSchema.addField("vsRB", Int::class.java)
            }
            if (!userSchema.hasField("vsRB_answered")) {
                userSchema.addField("vsRB_answered", Int::class.java)
            }
        }

        if (!schema.contains("DB_VS_RealM_Barcelona")) {
            schema.create("DB_VS_RealM_Barcelona")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("question", String::class.java)
                .addField("answer_A", String::class.java)
                .addField("answer_B", String::class.java)
                .addField("answer_C", String::class.java)
                .addField("answer_D", String::class.java)
                .addField("right_answer", String::class.java)
                .addField("answered", Boolean::class.java)
        }
    }
}
