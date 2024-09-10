package com.novumlogic.bookmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.novumlogic.bookmatch.screens.App
import com.novumlogic.bookmatch.screens.viewmodel.MainViewModel
import com.novumlogic.bookmatch.screens.viewmodel.ViewModelFactory
import com.novumlogic.bookmatch.ui.BookMatchTheme
import com.novumlogic.bookmatch.utils.GoogleSignInHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val signInHelper = GoogleSignInHelper(this)
            val viewModel by viewModels<MainViewModel> {  ViewModelFactory(signInHelper) }
            BookMatchTheme {
                App(viewModel = viewModel)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    BookMatchTheme {
        App()
    }
}