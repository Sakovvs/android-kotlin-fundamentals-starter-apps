package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private lateinit var wordList: MutableList<String>


    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf("queen", "hospital", "basketball", "cat", "change", "snail", "soup",
                "calendar", "sad", "desk", "guitar", "home", "railway", "zebra", "jelly", "car", "crow",
                "trade", "bag", "roll", "bubble"
        )
        wordList.shuffle()
    }


    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i("GameViewModel", "GameViewModel created")
    }

    private fun nextWord() {
        if (wordList.isEmpty()) {
            onGameFinish()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed")

    }

    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false

    }

}