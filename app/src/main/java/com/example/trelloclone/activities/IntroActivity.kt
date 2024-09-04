package com.example.trelloclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Typeface
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.trelloclone.R

class IntroActivity : BaseActivity() {
    private lateinit var btn_sign_up_intro: Button
    private lateinit var tv_app_name_intro: TextView
    private lateinit var btn_sign_in_intro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        btn_sign_up_intro = findViewById(R.id.btn_sign_up_intro)
        btn_sign_in_intro = findViewById(R.id.btn_sign_in_intro)
        tv_app_name_intro = findViewById(R.id.tv_app_name_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name_intro.typeface = typeface

        btn_sign_in_intro.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }


        btn_sign_up_intro.setOnClickListener {
            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}