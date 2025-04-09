package com.example.healthcaresystem.User.post

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "image_uris")

object ImageUriStore {
    private val IMAGE_URI_KEY = stringSetPreferencesKey("image_uris")

    fun getImageUris(context: Context): Flow<List<Uri>> {
        return context.dataStore.data.map { prefs ->
            prefs[IMAGE_URI_KEY]?.map { Uri.parse(it) } ?: emptyList()
        }
    }

    suspend fun saveImageUris(context: Context, uris: List<Uri>) {
        context.dataStore.edit { prefs ->
            prefs[IMAGE_URI_KEY] = uris.map { it.toString() }.toSet()
        }
    }
}
