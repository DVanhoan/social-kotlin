package com.hoan.frontend.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.hoan.frontend.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("access_token", null)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val navController = navHostFragment?.navController
            ?: throw IllegalStateException("NavController not found. Make sure that your layout contains a NavHostFragment with the correct id.")

        if (token.isNullOrEmpty()) {
            navController.navigate(R.id.action_global_signInFragment)
        } else {
            navController.navigate(R.id.action_global_mainFragment)
        }
    }
}
