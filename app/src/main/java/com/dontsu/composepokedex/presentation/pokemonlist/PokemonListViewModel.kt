package com.dontsu.composepokedex.presentation.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.dontsu.composepokedex.data.model.PokedexListEntry
import com.dontsu.composepokedex.data.repository.PokemonRepository
import com.dontsu.composepokedex.util.Constants.PAGE_SIZE
import com.dontsu.composepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var currentPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    var isSearching = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String) {
        // 검색중인 상태이면 포켓몬리스트의 value를 넣고, 그렇지 않으면 기존 caching된 리스트를 넣는다
        val listToSearch = if (isSearchStarting) {
            Timber.d("PokemonListViewModel : isSearching")
            pokemonList.value
        } else {
            Timber.d("PokemonListViewModel : isNotSearching")
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                Timber.d("PokemonListViewModel : query.isEmpty")
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) // 대소문자 신경쓰지않기
                        || it.number.toString() == query.trim() // 포켓몬 넘버로도 검색할 수 있으므로
            }
            if (isSearchStarting) {
                Timber.d("PokemonListViewModel : isSearching2")
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated() = viewModelScope.launch {
        isLoading.value = true
        val result = repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
        when (result) {
            is Resource.Success -> {
                endReached.value = currentPage * PAGE_SIZE >= result.data!!.count
                val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                    val number = if (entry.url.endsWith("/")) {
                        // dropLast(1)을 해서 슬래시(/)를 날리고 문자열이 digit이면 돌고 아니면 while 종료
                        // ex : https://pokeapi.co/api/v2/ability/7/"
                        // 뒤에서 부터 돌면서 확인 먼저 슬래시 날렷고 그다음이 7이니까 7은 digit이 맞다. 그 다음 /가 또 나오는데 이건 digit이 아니다. 그럼 7만 가져오고 whilte 종료된다.
                        entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                    } else {
                        entry.url.takeLastWhile { it.isDigit() }
                    }
                    val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                    PokedexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                }
                currentPage++

                loadError.value = ""
                isLoading.value = false
                pokemonList.value += pokedexEntries
            }
            is Resource.Error -> {
                loadError.value = result.message!!
                isLoading.value = false
            }
        }
    }

    fun calcDominantColor(
        drawable: Drawable,
        onFinish: (
            Color
        ) -> Unit
    ) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }

    }

}
