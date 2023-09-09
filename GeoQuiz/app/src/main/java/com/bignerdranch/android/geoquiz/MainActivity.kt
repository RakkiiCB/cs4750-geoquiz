package com.bignerdranch.android.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            val isCheated =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.assignCheated(isCheated)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener { view: View ->
            if (!quizViewModel.currentQuestionAnswered) {
                checkAnswer(true, view)
            }
        }

        binding.falseButton.setOnClickListener { view: View ->
            if (!quizViewModel.currentQuestionAnswered) {
                checkAnswer(false, view)
            }
        }

        binding.prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            updateQuestionCorrectText()

        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            updateQuestionCorrectText()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.resetButton.setOnClickListener{view: View ->
            resetQuestions(view)
        }

        binding.scoreButton.setOnClickListener { view: View ->
            showScore(view)
        }

        updateQuestion()
        updateQuestionCorrectText()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun updateQuestionCorrectText() {
        val questionCorrectId = when {
            !quizViewModel.currentQuestionAnswered
                -> R.string.unanswered_string
            quizViewModel.currentQuestionCheated
                -> R.string.judgment_string
            quizViewModel.currentQuestionCorrect
                -> R.string.correct_string
            else
                -> R.string.incorrect_string
        }

        binding.questionCorrectView.setText(questionCorrectId)
    }

    private fun checkAnswer(userAnswer: Boolean, view: View) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgment_string
            userAnswer == correctAnswer -> R.string.correct_string
            else -> R.string.incorrect_string
        }

        if (!quizViewModel.currentQuestionCheated) {
            quizViewModel.assignCorrect(userAnswer == correctAnswer)
        }

        quizViewModel.assignAnswered(true)
        updateQuestionCorrectText()
        Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT).show()
    }

    private fun resetQuestions(view: View) {
        quizViewModel.resetQuestions()
        Snackbar.make(view, R.string.reset_string, Snackbar.LENGTH_SHORT).show()
        updateQuestionCorrectText()
    }

    private fun showScore(view: View) {
        val txt = StringBuilder()
        txt.append(quizViewModel.getScore())
        txt.append("/")
        txt.append(quizViewModel.getQuestionBankSize())

        Snackbar.make(view, txt, Snackbar.LENGTH_SHORT).show()
    }
}
