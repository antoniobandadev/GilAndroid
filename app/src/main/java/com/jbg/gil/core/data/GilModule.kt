package com.jbg.gil.core.data

import android.content.Context
import androidx.room.Room
import com.jbg.gil.core.data.local.db.GilDataBase
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.daos.EventDao
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.data.remote.apis.EventApi
import com.jbg.gil.core.data.remote.apis.GuestApi
import com.jbg.gil.core.data.remote.apis.UserApi
import com.jbg.gil.core.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)

object GilModule {

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
    }.build()

    //Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //UserApi
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
    //ContactApi
    @Provides
    @Singleton
    fun provideContactApi(retrofit: Retrofit): ContactApi {
        return retrofit.create(ContactApi::class.java)
    }
    //EventApi
    @Provides
    @Singleton
    fun provideEventApi(retrofit: Retrofit): EventApi {
        return retrofit.create(EventApi::class.java)
    }
    //GuestApi
    @Provides
    @Singleton
    fun provideGuestApi(retrofit: Retrofit): GuestApi {
        return retrofit.create(GuestApi::class.java)
    }

    //Room
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GilDataBase {
        return Room.databaseBuilder(
            context,
            GilDataBase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    //ContactDao
    @Provides
    fun provideContactDao(db: GilDataBase): ContactDao = db.contactDao()

    //EventDao
    @Provides
    fun provideEventDao(db: GilDataBase): EventDao = db.eventDao()

}