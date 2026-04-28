package com.shyamdairyfarm.user.ui.di

import com.shyamdairyfarm.user.data.api.ApiService
import com.shyamdairyfarm.user.data.api.RetrofitClient
import com.shyamdairyfarm.user.data.repository.AddressRepository
import com.shyamdairyfarm.user.data.repository.AuthRepository
import com.shyamdairyfarm.user.data.repository.CartRepository
import com.shyamdairyfarm.user.data.repository.DeliveryCalendarRepository
import com.shyamdairyfarm.user.data.repository.DeliveryRepository
import com.shyamdairyfarm.user.data.repository.GeocoderRepository
import com.shyamdairyfarm.user.data.repository.OrderHistoryRepository
import com.shyamdairyfarm.user.data.repository.ProductRepository
import com.shyamdairyfarm.user.data.repository.SliderRepository
import com.shyamdairyfarm.user.data.repository.SubscriptionRepository
import com.shyamdairyfarm.user.data.repository.session.SessionRepository
import com.shyamdairyfarm.user.ui.viewmodel.AddressViewModel
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import com.shyamdairyfarm.user.ui.viewmodel.CartViewModel
import com.shyamdairyfarm.user.ui.viewmodel.DeliveryCalendarViewModel
import com.shyamdairyfarm.user.ui.viewmodel.DeliveryViewModel
import com.shyamdairyfarm.user.ui.viewmodel.HomeFragmentViewModel
import com.shyamdairyfarm.user.ui.viewmodel.HomeViewModel
import com.shyamdairyfarm.user.ui.viewmodel.InvoiceViewModel
import com.shyamdairyfarm.user.ui.viewmodel.NotificationViewModel
import com.shyamdairyfarm.user.ui.viewmodel.OrderHistoryViewModel
import com.shyamdairyfarm.user.ui.viewmodel.ProductDetailsViewModel
import com.shyamdairyfarm.user.ui.viewmodel.ProductSubscribeViewModel
import com.shyamdairyfarm.user.ui.viewmodel.SearchViewModel
import com.shyamdairyfarm.user.ui.viewmodel.SelectLocationViewModel
import com.shyamdairyfarm.user.ui.viewmodel.SubscriptionStatusViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
val appModule = module {
    // ApiService
    //    single { RetrofitClient.api }


            single {

                // 🔹 Logging interceptor (this prints headers)
                val loggingInterceptor = HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.HEADERS
                    // Use BODY if you want full request/response
                     level = HttpLoggingInterceptor.Level.BODY
                }

                val client = OkHttpClient.Builder()

                    // 🔹 Your interceptor (adds header)
                    .addInterceptor { chain ->
                        val originalRequest = chain.request()
                        val token = get<SessionRepository>().getToken()

                        val newRequest = originalRequest.newBuilder().apply {
                            token?.let {
                                addHeader("Authorization", it)
                            }
                        }.build()

                        // 👇 Optional manual log
                        println("➡️ Headers: ${newRequest.headers}")

                        chain.proceed(newRequest)
                    }

                    // 🔹 Logging comes AFTER header is added
                    .addInterceptor(loggingInterceptor)

                    .build()

                Retrofit.Builder()
                    .baseUrl(RetrofitClient.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
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
    single { SessionRepository(androidContext()) }

    single { AuthRepository(get()) }
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
    viewModel { AuthViewModel(get(), get(), get()) }
}