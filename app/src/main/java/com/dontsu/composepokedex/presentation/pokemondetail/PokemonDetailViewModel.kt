package com.dontsu.composepokedex.presentation.pokemondetail

import androidx.lifecycle.ViewModel
import com.dontsu.composepokedex.data.remote.response.Pokemon
import com.dontsu.composepokedex.data.repository.PokemonRepository
import com.dontsu.composepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }

}
