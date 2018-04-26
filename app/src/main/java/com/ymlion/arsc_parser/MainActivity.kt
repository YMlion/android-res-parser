package com.ymlion.arsc_parser

import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.ymlion.arsc_parser.R.id
import com.ymlion.arsc_parser.R.layout
import com.ymlion.arsc_parser.R.string
import com.ymlion.parser.ArscFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.Arrays
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
            var input = assets.open("resources.arsc")
            ArscFile.parse(input)
            input.close()

            input = assets.open("resources.arsc")
            if (!cacheDir.exists()) {
                cacheDir.mkdir()
            }
            val arscCopy = File(cacheDir, "resources.arsc")
            val out = BufferedOutputStream(FileOutputStream(arscCopy))
            val bytes = ByteArray(1024)
            while (input.read(bytes) != -1) {
                out.write(bytes)
            }
            out.flush()
            out.close()
            input.close()
            /*input = FileInputStream(arscCopy)
            val channel: FileChannel = input.channel
            channel.position(4)
            val byteBuffer = ByteBuffer.allocate(4)
            channel.read(byteBuffer)
            byteBuffer.flip()
            var i = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).int
            println("start with $i")
            channel.close()dddd
            input.close()*/
        }

        thread {
            SystemClock.sleep(2000)
            ArscFile(File(cacheDir, "resources.arsc")).parse()
        }

        thread {
            val byteFile = File(cacheDir, "test.dat")
            if (!byteFile.exists() || byteFile.length().toInt() == 0) {
                byteFile.createNewFile()
                val outputStream = FileOutputStream(byteFile)
                val channel = outputStream.channel
                var buffer = ByteBuffer.allocate(1024)
                val byteArray = ByteArray(1024)
                Arrays.fill(byteArray, 0x01.toByte())
                buffer.put(byteArray)
                buffer.flip()
                channel.write(buffer)
                buffer = Charset.forName("utf-8").encode("hello world!")
                channel.write(buffer)
                outputStream.close()
                channel.close()
            }
            SystemClock.sleep(1000)
            val raf = RandomAccessFile(byteFile, "rw")
            raf.seek(1024)
            raf.writeChars("hello, random!")
            raf.write(0x7f)
            raf.close()
        }
    }
}
