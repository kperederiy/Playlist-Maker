package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val buttonBack = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            finish()
        }

        val inputSearchText = findViewById<EditText>(R.id.inputSearchText)
        val btnClearSearch = findViewById<ImageView>(R.id.btnClearSearch)

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // можно добавить логику перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearSearch.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                // сюда можно добавить, например, debounce или фильтрацию
            }
        })

        btnClearSearch.setOnClickListener {
            inputSearchText.text.clear()
            btnClearSearch.visibility = View.GONE

            // Скрываем клавиатуру
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
        }
    }
}