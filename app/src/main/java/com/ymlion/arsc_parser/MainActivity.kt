package com.ymlion.arsc_parser

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.ymlion.arsc_parser.R.id
import com.ymlion.arsc_parser.R.layout
import com.ymlion.arsc_parser.R.string
import com.ymlion.parser.ArscFile
import kotlinx.android.synthetic.main.activity_main.message
import kotlinx.android.synthetic.main.activity_main.navigation

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            id.navigation_home -> {
                message.setText(string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            id.navigation_dashboard -> {
                message.setText(string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            id.navigation_notifications -> {
                message.setText(string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val input = assets.open("resources1.arsc")
        ArscFile.parse(input)
        input.close()
    }
}
