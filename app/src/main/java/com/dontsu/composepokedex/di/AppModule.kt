package com.dontsu.composepokedex.di

import com.dontsu.composepokedex.data.remote.PokeApi
import com.dontsu.composepokedex.data.repository.PokemonRepository
import com.dontsu.composepokedex.util.Url.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ) = PokemonRepository(api, ioDispatcher)

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create()
    }

}
