package com.misaventuras.di

import android.content.Context
import androidx.room.Room
import com.misaventuras.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "adventures.db")
            // Early development builds used version 1 for more than one schema. Room
            // cannot open those installations because their identity hash no longer
            // matches. Recreate that prototype database once instead of crashing on
            // every launch; future schema changes must provide regular migrations.
            .fallbackToDestructiveMigrationFrom(1)
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        "INSERT INTO profiles(id,name,avatarPath,primaryColor,secondaryColor,background,completionImagePath) " +
                            "VALUES(1,'Lola',NULL,4285945512,4294292946,'clouds',NULL)," +
                            "(2,'Olivia',NULL,4279766152,4294957174,'bubbles',NULL)",
                    )
                }
            })
            .build()

    @Provides
    fun dao(db: AppDatabase) = db.dao()
}
