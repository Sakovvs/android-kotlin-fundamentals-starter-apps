/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//https://codelabs.developers.google.com/codelabs/kotlin-android-training-room-database/#5

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class],
        version = 1,
        exportSchema = false)
public abstract class SleepDatabase : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {
        @Volatile                                                           // Значение Volatile (изменчивой) переменной никогда не будет кэшироваться, и все операции записи и чтения будут выполняться в и из основной памяти. Это означает, что изменения, сделанные одним потоком INSTANCE, сразу видны всем другим потокам, и вы не получите ситуацию, когда, скажем, два потока каждый обновляют одну и ту же сущность в кеше, что может создать проблему.
        private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()               // стратегия миграции ---- уничтожить и восстановить базу данных
                            .build()
                }
                return instance
            }
        }
    }
}
