package com.example.todoapp.ui

import android.app.Application
import com.example.todoapp.ui.alltasks.AllTasksViewModel
import com.example.todoapp.ui.settings.SettingsViewModel
import com.example.todoapp.ui.taskdetail.TaskDetailViewModel
import com.example.todoapp.ui.taskform.TaskFormViewModel
import com.example.todoapp.ui.today.TodayViewModel
import com.example.todoapp.ui.upcoming.UpcomingViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test

class ViewModelInstantiationTest {

    @Test
    fun testViewModelsHaveApplicationConstructor() {
        assertNotNull(TodayViewModel::class.java.getConstructor(Application::class.java))
        assertNotNull(UpcomingViewModel::class.java.getConstructor(Application::class.java))
        assertNotNull(AllTasksViewModel::class.java.getConstructor(Application::class.java))
        assertNotNull(TaskFormViewModel::class.java.getConstructor(Application::class.java))
        assertNotNull(TaskDetailViewModel::class.java.getConstructor(Application::class.java))
        assertNotNull(SettingsViewModel::class.java.getConstructor(Application::class.java))
    }
}
