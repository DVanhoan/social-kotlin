package com.hoan.client.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hoan.client.database.converter.ImageBitmapString
import com.hoan.client.database.dao.ImageCacheDao
import com.hoan.client.database.model.ImageCache

@Database(
    entities = [ImageCache::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    ImageBitmapString::class
)
abstract class ImageCacheDatabase : RoomDatabase() {
    abstract fun imageCacheDao(): ImageCacheDao
}