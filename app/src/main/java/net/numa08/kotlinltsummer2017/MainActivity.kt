package net.numa08.kotlinltsummer2017

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import java.io.Closeable


class VeryHeavyFileLoadingInterface(private val fileName: String) : Closeable {

    private var _loadedFile: ByteArray? = null
    var loadedFile: ByteArray? = null
        get() {
            if (closed) {
                throw RuntimeException("Already closed")
            }
            return _loadedFile
        }

    var closed : Boolean = false
        private set

    fun load() {
        val f = fileName.toByteArray()
        _loadedFile = f
    }

    override fun close() {
        closed = true
    }

}

class MainActivity : AppCompatActivity() {

    private val fileInterface: VeryHeavyFileLoadingInterface by lazy { VeryHeavyFileLoadingInterface(targetFileName) }
    private val isCompleted: Boolean by lazy { fileInterface.loadedFile != null }
    private val text: TextView by lazy { findViewById(R.id.text) as TextView }

    private val targetFileName: String
        get() =
            intent?.getStringExtra("file_name") ?: "test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.text = "start"
        Log.d("kotlin_lt", "start file load $isCompleted")
        Thread(Runnable {
            Thread.sleep(10 * 1000L)
            fileInterface.load()
            Handler(Looper.getMainLooper()).post {
                text.text = "completed"
                Log.d("kotlin_lt", "complete file load $isCompleted, size is ${fileInterface.loadedFile?.size}")
            }
        }).start()

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        fileInterface.close()
        super.onDestroy()
    }

}
