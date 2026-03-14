package com.example.freshyzoappmodule.ui.di

import com.example.freshyzoappmodule.data.api.RetrofitClient
import com.example.freshyzoappmodule.data.repository.SubscriptionRepository
import com.example.freshyzoappmodule.viewmodel.SubscriptionStatusViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ApiService
    single { RetrofitClient.api }
    // Repository
    single { SubscriptionRepository(get()) }
    // ViewModel
    viewModel { SubscriptionStatusViewModel(get()) }

}