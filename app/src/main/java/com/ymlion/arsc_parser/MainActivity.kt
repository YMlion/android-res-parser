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
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.concurrent.thread

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
        thread {
            var input = assets.open("resources1.arsc")
            ArscFile.parse(input)
            input.close()

            input = assets.open("resources1.arsc")
            if (!cacheDir.exists()) {
                cacheDir.mkdir()
            }
            val arscCopy = File(cacheDir, "resources1.arsc")
            val out = BufferedOutputStream(FileOutputStream(arscCopy))
            val bytes = ByteArray(1024)
            while (input.read(bytes) != -1) {
                out.write(bytes)
            }
            out.flush()
            out.close()
            input.close()
            input = FileInputStream(arscCopy)
            var channel: FileChannel = input.channel
            channel.position(4)
            var byteBuffer = ByteBuffer.allocate(4)
            channel.read(byteBuffer)
            byteBuffer.flip()
            var i = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).int
            println("start with $i")
            channel.close()
            byteBuffer.clear()
            input.close()
        }
    }
}
