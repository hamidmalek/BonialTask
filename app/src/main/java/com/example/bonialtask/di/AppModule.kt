package com.example.bonialtask.di

import android.app.Application
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.network.ApiService
import com.example.bonialtask.repo.BrochureRepository
import com.example.bonialtask.ui.brochure.BrochureViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        val moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory
                    .of(ContentWrapper::class.java, "contentType")
                    .withSubtype(
                        ContentWrapper.SuperBannerCarousel::class.java,
                        "superBannerCarousel"
                    )
                    .withSubtype(
                        ContentWrapper.Brochure::class.java,
                        "brochure"
                    )
                    .withSubtype(
                        ContentWrapper.Brochure::class.java,
                        "brochurePremium"
                    )
            )
            .add(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
}
val repositoryModule = module { single { BrochureRepository(get()) } }
val imageModule = module {
    single {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .respectCacheHeaders(true)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
}
val brochureModule = module {
    viewModel { BrochureViewModel(get()) }
}

fun Application.initKoin() = startKoin {
    androidContext(this@initKoin)
    modules(networkModule, repositoryModule, imageModule, brochureModule)
}