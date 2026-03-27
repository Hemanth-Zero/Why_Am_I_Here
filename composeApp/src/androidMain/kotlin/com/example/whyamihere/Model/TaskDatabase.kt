package com.example.whyamihere.Model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues


 //SQLite database for Tasks – insert and delete . Kept simple (no Room) to avoid extra gradle deps.

data class Task(
    val id: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

class TaskDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                $COL_ID   INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_TS   INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun insertTask(title: String): Task {
        val db  = writableDatabase
        val now = System.currentTimeMillis()
        val cv  = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_TS, now)
        }
        val id = db.insert(TABLE, null, cv)
        return Task(id, title, now)
    }

    fun deleteTask(id: Long) {
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun getAllTasks(): List<Task> {
        val db     = readableDatabase
        val cursor = db.query(TABLE, null, null, null, null, null, "$COL_TS DESC")
        val list   = mutableListOf<Task>()
        cursor.use { c ->
            while (c.moveToNext()) {
                list += Task(
                    id        = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                    title     = c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                    createdAt = c.getLong(c.getColumnIndexOrThrow(COL_TS))
                )
            }
        }
        return list
    }

    fun hasTasks(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE", null)
        cursor.use { return it.moveToFirst() && it.getInt(0) > 0 }
    }

    companion object {
        private const val DB_NAME    = "tasks.db"
        private const val DB_VERSION = 1
        const val TABLE     = "tasks"
        const val COL_ID    = "id"
        const val COL_TITLE = "title"
        const val COL_TS    = "created_at"

        @Volatile private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskDatabase(context).also { INSTANCE = it }
            }
    }
}
