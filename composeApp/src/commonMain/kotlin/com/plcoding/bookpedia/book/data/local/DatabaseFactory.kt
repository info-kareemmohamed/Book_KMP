package com.plcoding.bookpedia.book.data.local

import androidx.room.RoomDatabase

expect class DatabaseFactory {
fun create():RoomDatabase.Builder<FavoriteBookDatabase>
}