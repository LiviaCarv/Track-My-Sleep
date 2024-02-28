package com.project.sleeptracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.project.sleeptracker.database.SleepDatabase
import com.project.sleeptracker.database.SleepDatabaseDao
import com.project.sleeptracker.database.SleepNight
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java).allowMainThreadQueries().build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetNight() {
        // Arrange
        val night = SleepNight()

        // Act
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()

        // Assert
        assertEquals(-1, tonight?.sleepQuality)
    }
}