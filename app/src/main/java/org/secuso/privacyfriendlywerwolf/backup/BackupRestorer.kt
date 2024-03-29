package org.secuso.privacyfriendlywerwolf.backup

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.JsonReader
import android.util.Log
import androidx.annotation.NonNull
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.FileUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess


class BackupRestorer : IBackupRestorer {

    @Throws(IOException::class)
    private fun readPreferences(reader: JsonReader, preferences: SharedPreferences.Editor) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name: String = reader.nextName()
            Log.d("preference", name)
            when (name) {
                "pref_seer_player",
                "pref_witch_player",
                "pref_sound_background" -> preferences.putBoolean(name, reader.nextBoolean())
                "pref_timer_witch",
                "pref_werewolf_player",
                "pref_timer_seer",
                "pref_timer_night",
                "pref_timer_day" -> preferences.putInt(name, reader.nextInt())
                else -> throw RuntimeException("Unknown preference $name")
            }
        }
        reader.endObject()
    }

    private fun readPreferenceSet(reader: JsonReader): Set<String> {
        val preferenceSet = mutableSetOf<String>()

        reader.beginArray()
        while (reader.hasNext()) {
            preferenceSet.add(reader.nextString());
        }
        reader.endArray()
        return preferenceSet
    }

    override fun restoreBackup(context: Context, restoreData: InputStream): Boolean {
        return try {
            val isReader = InputStreamReader(restoreData)
            val reader = JsonReader(isReader)
            val preferences = PreferenceManager.getDefaultSharedPreferences(context).edit()

            // START
            reader.beginObject()
            while (reader.hasNext()) {
                val type: String = reader.nextName()
                when (type) {
                    "preferences" -> readPreferences(reader, preferences)
                    else -> throw RuntimeException("Can not parse type $type")
                }
            }
            reader.endObject()
            preferences.commit()

            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val TAG = "PFABackupRestorer"
    }
}