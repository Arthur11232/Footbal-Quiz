package com.arthuralexandryan.footballquiz.db_app

import android.util.Log
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class DB_Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Log.e("FQ_DB", "oldVersion: $oldVersion newVersion: $newVersion")
        val schema = realm.schema
        if (oldVersion == 1L) {
            val userSchema = schema.get("FQ_Scores")!!
            userSchema.renameField("versus", "vsRM")
            userSchema.renameField("versus_answered", "vsRM_answered")
            userSchema.addField("vsRB", Int::class.java)
            userSchema.addField("vsRB_answered", Int::class.java)

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
