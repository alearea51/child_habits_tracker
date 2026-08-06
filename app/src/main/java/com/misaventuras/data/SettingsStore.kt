package com.misaventuras.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")
@Singleton class SettingsStore @Inject constructor(@ApplicationContext private val context:Context){
 private val selected=longPreferencesKey("selected_profile"); private val animations=booleanPreferencesKey("animations"); private val pin=stringPreferencesKey("adult_pin")
 val selectedProfile=context.dataStore.data.map{it[selected]}; val animationsEnabled=context.dataStore.data.map{it[animations]?:true}; val adultPin=context.dataStore.data.map{it[pin]}
 suspend fun select(id:Long)=context.dataStore.edit{it[selected]=id}; suspend fun setAnimations(value:Boolean)=context.dataStore.edit{it[animations]=value}; suspend fun setPin(value:String?)=context.dataStore.edit{if(value==null)it.remove(pin) else it[pin]=value}
}
