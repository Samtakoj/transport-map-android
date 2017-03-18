package com.samtakoj.schedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            textView("Hello world!") {
                textSize = 24f
            }
            button("Click") {
                onClick {
                    toast("Hi!")
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }
    }
}