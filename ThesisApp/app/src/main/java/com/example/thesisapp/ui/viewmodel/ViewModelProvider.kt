package com.example.thesisapp.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thesisapp.ThesisApplication

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ApplicationViewModel(
                thesisApplication().container.localNewsRepository,
                thesisApplication().container.modelRepository
            )
        }
    }
}


fun CreationExtras.thesisApplication(): ThesisApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ThesisApplication)