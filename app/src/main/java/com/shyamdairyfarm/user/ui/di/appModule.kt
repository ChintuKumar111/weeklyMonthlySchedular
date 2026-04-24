package com.shyamdairyfarm.user.ui.di

import com.shyamdairyfarm.user.data.api.RetrofitClient
import com.shyamdairyfarm.user.data.repository.*
import com.shyamdairyfarm.user.data.repository.session.SessionRepository
import com.shyamdairyfarm.user.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

val appModule = module {
    // ApiService
    single { RetrofitClient.api }
    // Repositories
    single { SubscriptionRepository(get()) }
    single { OrderHistoryRepository(get()) }
    single { ProductRepository(get()) }
    single { SliderRepository(get()) }
    single { DeliveryCalendarRepository(get()) }
    single { AddressRepository(get()) }
    single { CartRepository(androidContext()) }
    single { DeliveryRepository(get()) }
    single { GeocoderRepository(androidContext()) }
    single{ SessionRepository(androidContext()) }

    single{ AuthRepository(get()) }
    // ViewModels
    viewModel { SubscriptionStatusViewModel(get()) }
    viewModel { OrderHistoryViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { HomeFragmentViewModel(get()) }
    viewModel { SearchViewModel(androidApplication()) }
    viewModel { AddressViewModel(get()) }
    viewModel { DeliveryCalendarViewModel(get()) }
    viewModel { ProductSubscribeViewModel() }
    viewModel { ProductDetailsViewModel() }
    viewModel { CartViewModel() }
    //====================
    viewModel { DeliveryViewModel(get()) }
    viewModel { NotificationViewModel(androidApplication()) }
    viewModel { InvoiceViewModel() }
    viewModel { SelectLocationViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
}