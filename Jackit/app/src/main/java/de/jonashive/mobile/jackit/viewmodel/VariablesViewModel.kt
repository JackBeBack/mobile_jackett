package de.jonashive.mobile.jackit

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel

class VariablesViewModel(activity: MainActivity) : ViewModel() {
    lateinit var sharedPref: SharedPreferences

    companion object {
        lateinit var singelton: VariablesViewModel
    }

    init {
        sharedPref = activity?.getSharedPreferences("Main", Context.MODE_PRIVATE)
        singelton = this
    }

    fun write(v: Variable, str: String){
        with (sharedPref.edit()) {
            putString(v.toString(), str)
            apply()
        }
    }

    fun read(v: Variable): String {
        return sharedPref.getString(v.toString(), "") ?: ""
    }

}

enum class Variable {
    BASE_URL,
    PORT,
    JACKETT_API_KEY
}