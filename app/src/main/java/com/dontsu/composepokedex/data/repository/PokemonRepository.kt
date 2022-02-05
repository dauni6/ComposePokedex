package com.dontsu.composepokedex.data.repository

import com.dontsu.composepokedex.data.remote.PokeApi
import com.dontsu.composepokedex.data.remote.response.Pokemon
import com.dontsu.composepokedex.data.remote.response.PokemonList
import com.dontsu.composepokedex.di.IoDispatcher
import com.dontsu.composepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> = withContext(ioDispatcher) {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return@withContext Resource.Error("알 수 없는 에러가 발생했습니다.")
        }
        return@withContext Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> = withContext(ioDispatcher) {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return@withContext Resource.Error("알 수 없는 에러가 발생했습니다.")
        }
        return@withContext Resource.Success(response)
    }

}
