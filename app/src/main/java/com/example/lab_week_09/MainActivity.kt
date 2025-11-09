package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab_week_09.R
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here, we use setContent instead of setContentView
        setContent {
            //Here, we wrap our content with the theme
            //You can check out the LAB_WEEK_09Theme inside Theme.kt
            LAB_WEEK_09Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(
                        navController = navController
                    )
                }
            }
        }
    }
}
//Here, instead of defining it in an XML file,
//we create a composable function called Home
//@Composable is used to tell the compiler that this is a composable function
//It's a way of defining a composable
data class Student(
    var name: String
)

@Composable
fun ResultContent(jsonData: String) {
    val moshi = remember {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    val adapter = remember {
        val listType = Types.newParameterizedType(List::class.java, Student::class.java)
        moshi.adapter<List<Student>>(listType)
    }
    val studentList = try {
        adapter.fromJson(jsonData) ?: emptyList()
    } catch (e: Exception) {
        emptyList<Student>()
    }

    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OnBackgroundTitleText(text = "Student List")
        }
        items(studentList) { student ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = student.name)
            }
        }
    }
}

//Here, we create a composable function called App
//This will be the root composable of the app
@Composable
fun App(navController: NavHostController) {
    //Here, we use NavHost to create a navigation graph
    //We pass the navController as a parameter
    //We also set the startDestination to "home"
    //This means that the app will start with the Home composable
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        //Here, we create a route called "home"
        //We pass the Home composable as a parameter
        //This means that when the app navigates to "home",
        //the Home composable will be displayed
        composable("home") {
            //Here, we pass a lambda function that navigates to
            "resultContent"
            //and pass the jsonData as a parameter
            Home { navController.navigate(
                "resultContent/?jsonData=$it")
            }
        }
        //Here, we create a route called "resultContent"
        //We pass the ResultContent composable as a parameter
        //This means that when the app navigates to "resultContent",
        //the ResultContent composable will be displayed
        //You can also define arguments for the route
        //Here, we define a String argument called "listData"
        //We use navArgument to define the argument
        //We use NavType.StringType to define the type of the argument
        composable(
            "resultContent/?jsonData={jsonData}",
            arguments = listOf(navArgument("jsonData") {
                type = NavType.StringType }
            )
        ) {
            //Here, we pass the value of the argument to the ResultContent
            ResultContent(
                it.arguments?.getString("jsonData").orEmpty()
            )
        }
    }
}

@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    val listData = remember { mutableStateListOf<Student>() }
    var inputField by remember { mutableStateOf(Student("")) }

    val moshi = remember {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    val adapter = remember {
        val listType = Types.newParameterizedType(List::class.java, Student::class.java)
        moshi.adapter<List<Student>>(listType)
    }

    //Here, we use LazyColumn to display a list of items lazily
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(
                    text = stringResource(
                        id = R.string.enter_item
                    )
                )
                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        inputField = Student(it)
                    }
                )
                Row {
                    PrimaryTextButton(
                        text = stringResource(
                            id =
                                R.string.button_click
                        )
                    ) {
                        if (inputField.name.trim().isNotEmpty()) {
                            listData.add(Student(inputField.name.trim()))
                            inputField = Student("")
                        }
                    }
                    PrimaryTextButton(
                        text = stringResource(
                            id =
                                R.string.button_navigate
                        )
                    ) {
                        val jsonString = adapter.toJson(listData.toList())
                        navigateFromHomeToResult(jsonString)
                    }
                }
            }
        }
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

//Here, we create a preview function of the Home composable
//This function is specifically used to show a preview of the Home composable
//This is only for development purpose
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home(navigateFromHomeToResult = { /* Preview - no navigation */ })
    }
}