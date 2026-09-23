package com.kidsworkbook.app.ui.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.ui.screens.home.DashboardScreen
import com.kidsworkbook.app.ui.screens.onboarding.ProfileSetupScreen
import com.kidsworkbook.app.ui.screens.performance.PerformanceScreen
import com.kidsworkbook.app.ui.screens.subject.ActivityScreen
import com.kidsworkbook.app.ui.screens.subject.ResultScreen
import com.kidsworkbook.app.ui.screens.subject.SubjectListScreen
import com.kidsworkbook.app.viewmodel.ContentViewModel
import com.kidsworkbook.app.viewmodel.PerformanceViewModel
import com.kidsworkbook.app.viewmodel.ProfileViewModel
import com.kidsworkbook.app.viewmodel.ViewModelFactory

private object Routes {
    const val PROFILE_SETUP = "profile_setup"
    const val DASHBOARD = "dashboard"
    const val SUBJECT = "subject/{subjectName}"
    const val ACTIVITY = "activity/{subjectName}/{activityId}"
    const val RESULT = "result/{correct}/{total}/{duration}"
    const val PERFORMANCE = "performance"
    fun subject(s: Subject) = "subject/${s.name}"
    fun activity(s: Subject, activityId: String) = "activity/${s.name}/$activityId"
    fun result(correct: Int, total: Int, duration: Long) = "result/$correct/$total/$duration"
}

@Composable
fun WorkbookNavGraph(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val performanceViewModel: PerformanceViewModel = viewModel(factory = factory)
    val contentViewModel: ContentViewModel = viewModel(factory = factory)

    val activeStudent by profileViewModel.activeStudent.collectAsState()

    LaunchedEffect(activeStudent?.id) {
        activeStudent?.id?.let { performanceViewModel.setActiveStudent(it) }
    }

    NavHost(navController = navController, startDestination = Routes.PROFILE_SETUP) {

        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                viewModel = profileViewModel,
                onProfileSaved = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            val student = activeStudent
            val totalAttempts by performanceViewModel.totalAttempts.collectAsState()
            if (student == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                DashboardScreen(
                    student = student,
                    totalAttempts = totalAttempts,
                    onSubjectClick = { subject ->
                        contentViewModel.loadActivities(subject, student.age)
                        navController.navigate(Routes.subject(subject))
                    },
                    onViewProgress = { navController.navigate(Routes.PERFORMANCE) },
                    onEditProfile = { navController.navigate(Routes.PROFILE_SETUP) }
                )
            }
        }

        composable(
            Routes.SUBJECT,
            arguments = listOf(navArgument("subjectName") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: return@composable
            val subject = Subject.valueOf(subjectName)
            val activities by contentViewModel.activities.collectAsState()
            SubjectListScreen(
                subject = subject,
                activities = activities,
                onActivityClick = { activity ->
                    contentViewModel.loadActivity(subject, activity.id)
                    navController.navigate(Routes.activity(subject, activity.id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.ACTIVITY,
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType },
                navArgument("activityId") { type = NavType.StringType }
            )
        ) {
            val activity by contentViewModel.currentActivity.collectAsState()
            val student = activeStudent
            if (activity == null || student == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                ActivityScreen(
                    activity = activity!!,
                    onComplete = { correct, total, durationSeconds ->
                        performanceViewModel.recordResult(
                            studentId = student.id,
                            subject = activity!!.subject,
                            activityId = activity!!.id,
                            activityTitle = activity!!.title,
                            correctAnswers = correct,
                            totalQuestions = total,
                            durationSeconds = durationSeconds
                        )
                        contentViewModel.clearCurrentActivity()
                        navController.navigate(Routes.result(correct, total, durationSeconds)) {
                            popUpTo(Routes.DASHBOARD)
                        }
                    },
                    onExit = {
                        contentViewModel.clearCurrentActivity()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            Routes.RESULT,
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("duration") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val duration = backStackEntry.arguments?.getLong("duration") ?: 0
            ResultScreen(
                correctAnswers = correct,
                totalQuestions = total,
                durationSeconds = duration,
                completedAtMillis = System.currentTimeMillis(),
                onDone = { navController.popBackStack(Routes.DASHBOARD, inclusive = false) }
            )
        }

        composable(Routes.PERFORMANCE) {
            val averages by performanceViewModel.subjectAverages.collectAsState()
            val history by performanceViewModel.history.collectAsState()
            PerformanceScreen(
                subjectAverages = averages,
                history = history,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
