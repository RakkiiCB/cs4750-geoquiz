package com.bignerdranch.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
// const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_numpanels,true),
        Question(R.string.question_a3america,true),
        Question(R.string.question_taylorswift,false),
        Question(R.string.question_justinbieber,true),
        Question(R.string.question_mariomix,true),
        Question(R.string.question_hardestdiff, false),
        Question(R.string.question_aarequirement,false),
        Question(R.string.question_perfectjudgment,false),
        Question(R.string.question_europeanname,true),
        Question(R.string.question_fullcombo,true)
    )

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionCorrect: Boolean
        get() = questionBank[currentIndex].correct

    val currentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].answered

    val currentQuestionCheated: Boolean
        get() = questionBank[currentIndex].cheated

    fun moveToPrev() {
        currentIndex = if (currentIndex == 0) {
            questionBank.size - 1
        } else {
            currentIndex - 1
        }
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun assignAnswered(newAnswered : Boolean) {
        questionBank[currentIndex].answered = newAnswered
    }

    fun assignCorrect(newCorrect : Boolean) {
        questionBank[currentIndex].correct = newCorrect
    }

    fun assignCheated(newCheated : Boolean) {
        questionBank[currentIndex].cheated = newCheated
    }

    fun resetQuestions() {
        for (question in questionBank) {
            question.answered = false
            question.correct = false
        }
    }

    fun getScore(): Int {
        var correctAnswers: Int = 0
        for (question in questionBank) {
            if (question.correct) {
                correctAnswers++
            }
        }
        return correctAnswers
    }

    fun getQuestionBankSize(): Int {
        return questionBank.size
    }

}