package com.plcoding.bookpedia.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailAction
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailScreenRoot
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailViewModel
import com.plcoding.bookpedia.book.presentation.book_list.BookListScreenRoot
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.BookGraph) {
            navigation<Routes.BookGraph>(startDestination = Routes.BookList) {

                composable<Routes.BookList>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    val sharedViewModel = it.sharedViewModel<SelectedBookViewModel>(navController)

                    LaunchedEffect(true) {
                        sharedViewModel.setSelectedBook(null)
                    }

                    BookListScreenRoot(
                        viewModel = koinViewModel<BookListViewModel>(),
                        onBookClick = {
                            sharedViewModel.setSelectedBook(it)
                            navController.navigate(Routes.BookDetail(it.id))
                        }
                    )
                }

                composable<Routes.BookDetail>(
                    enterTransition = {
                        slideInHorizontally { initialOffset ->
                            initialOffset
                        }
                    },
                    exitTransition = {
                        slideOutHorizontally { initialOffset ->
                            initialOffset
                        }
                    }
                ) {
                    val sharedViewModel = it.sharedViewModel<SelectedBookViewModel>(navController)
                    val selectedBook by
                    sharedViewModel.selectedBook.collectAsStateWithLifecycle()

                    val viewModel = koinViewModel<BookDetailViewModel>()

                    LaunchedEffect(selectedBook) {
                        selectedBook?.let { book ->
                            viewModel.onAction(BookDetailAction.OnSelectedBookChange(book))
                        }
                    }

                    BookDetailScreenRoot(
                        viewModel = koinViewModel<BookDetailViewModel>(),
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }

            }


        }

    }
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraph = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraph)
    }

    return koinViewModel(viewModelStoreOwner = parentEntry)
}