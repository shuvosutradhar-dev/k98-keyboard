package com.pcpower.k98keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Button

class K98InputMethodService : InputMethodService() {

    private lateinit var soundManager: MechanicalSoundManager
    private var isCaps = false
    private var isSymbols = false

    private val letterKeyIds = listOf(
        R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t,
        R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
        R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g,
        R.id.key_h, R.id.key_j, R.id.key_k, R.id.key_l,
        R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v, R.id.key_b,
        R.id.key_n, R.id.key_m
    )

    private val numberKeyIds = listOf(
        R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4, R.id.key_5,
        R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_0
    )

    override fun onCreate() {
        super.onCreate()
        soundManager = MechanicalSoundManager(this)
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_layout, null)
        setupKeyboard(view)
        return view
    }

    private fun setupKeyboard(rootView: View) {
        // Number row keys
        for (id in numberKeyIds) {
            rootView.findViewById<Button>(id)?.setOnClickListener { btn ->
                val char = (btn as Button).text.toString()
                pressKey(char)
            }
        }

        // Letter keys
        for (id in letterKeyIds) {
            rootView.findViewById<Button>(id)?.setOnClickListener { btn ->
                val char = (btn as Button).text.toString()
                pressKey(char)
            }
        }

        // Punctuation keys
        rootView.findViewById<Button>(R.id.key_comma)?.setOnClickListener { pressKey(",") }
        rootView.findViewById<Button>(R.id.key_dot)?.setOnClickListener { pressKey(".") }

        // Space key
        rootView.findViewById<Button>(R.id.key_space)?.setOnClickListener {
            soundManager.playClick()
            currentInputConnection?.commitText(" ", 1)
        }

        // Backspace key
        rootView.findViewById<Button>(R.id.key_backspace)?.setOnClickListener {
            soundManager.playClick()
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        // Enter key
        rootView.findViewById<Button>(R.id.key_enter)?.setOnClickListener {
            soundManager.playClick()
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
            )
        }

        // Shift key (Toggle Upper/Lower case)
        val shiftBtn = rootView.findViewById<Button>(R.id.key_shift)
        shiftBtn?.setOnClickListener {
            soundManager.playClick()
            isCaps = !isCaps
            shiftBtn.text = if (isCaps) "⬆" else "⇧"
            for (id in letterKeyIds) {
                val b = rootView.findViewById<Button>(id) ?: continue
                val text = b.text.toString()
                b.text = if (isCaps) text.uppercase() else text.lowercase()
            }
        }

        // Symbols toggle (?123)
        val symbolBtn = rootView.findViewById<Button>(R.id.key_symbols)
        symbolBtn?.setOnClickListener {
            soundManager.playClick()
            isSymbols = !isSymbols
            symbolBtn.text = if (isSymbols) "ABC" else "?123"
            updateSymbolLayout(rootView, isSymbols)
        }
    }

    private fun updateSymbolLayout(rootView: View, showSymbols: Boolean) {
        val symbolMap = mapOf(
            R.id.key_q to "@", R.id.key_w to "#", R.id.key_e to "$", R.id.key_r to "%",
            R.id.key_t to "&", R.id.key_y to "-", R.id.key_u to "+", R.id.key_i to "(",
            R.id.key_o to ")", R.id.key_p to "/", R.id.key_a to "*", R.id.key_s to "\"",
            R.id.key_d to "'", R.id.key_f to ":", R.id.key_g to ";", R.id.key_h to "!",
            R.id.key_j to "?", R.id.key_k to "~", R.id.key_l to "_", R.id.key_z to "<",
            R.id.key_x to ">", R.id.key_c to "=", R.id.key_v to "[", R.id.key_b to "]",
            R.id.key_n to "{", R.id.key_m to "}"
        )
        val defaultLetters = mapOf(
            R.id.key_q to "q", R.id.key_w to "w", R.id.key_e to "e", R.id.key_r to "r",
            R.id.key_t to "t", R.id.key_y to "y", R.id.key_u to "u", R.id.key_i to "i",
            R.id.key_o to "o", R.id.key_p to "p", R.id.key_a to "a", R.id.key_s to "s",
            R.id.key_d to "d", R.id.key_f to "f", R.id.key_g to "g", R.id.key_h to "h",
            R.id.key_j to "j", R.id.key_k to "k", R.id.key_l to "l", R.id.key_z to "z",
            R.id.key_x to "x", R.id.key_c to "c", R.id.key_v to "v", R.id.key_b to "b",
            R.id.key_n to "n", R.id.key_m to "m"
        )

        for ((id, char) in if (showSymbols) symbolMap else defaultLetters) {
            val b = rootView.findViewById<Button>(id) ?: continue
            b.text = if (!showSymbols && isCaps) char.uppercase() else char
        }
    }

    private fun pressKey(char: String) {
        soundManager.playClick()
        currentInputConnection?.commitText(char, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
