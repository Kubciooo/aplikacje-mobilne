package com.example.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val c = Calendar.getInstance()
            c.time = Date()

            c.set(2021, 6, 5, 20, 0);
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes", dueTime=c.timeInMillis))
                dao.insert(Task("Do the laundry", dueTime=c.timeInMillis+10000))
                dao.insert(Task("Buy groceries", dueTime=c.timeInMillis+1234567))
                dao.insert(Task("get a job", important = true, dueTime=c.timeInMillis+123321))
                dao.insert(Task("build a house", completed = true, dueTime=c.timeInMillis+1222121))
                dao.insert(Task("build a second house", completed = true, important = true, dueTime=c.timeInMillis+12311))
                dao.insert(Task("get a girlfriend", completed = true, dueTime=c.timeInMillis+123211))
            }

        }
    }
}