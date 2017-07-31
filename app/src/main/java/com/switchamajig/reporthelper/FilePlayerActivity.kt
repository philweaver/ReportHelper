package com.switchamajig.reporthelper

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.File
import java.io.FilenameFilter

class FilePlayerActivity : Activity() {
    companion object {
        const val TAG = "FilePlayerActivity"
        const val KEY_DIRECTORY = "com.switchamajig.reporthelper.FilePlayerActivity.directory"
    }

    private var mediaPlayer : MediaPlayer? = null
    private lateinit var directory : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val directoryFromIntent = intent.extras.getCharSequence(KEY_DIRECTORY)
        if (directoryFromIntent != null) {
            Log.v(TAG, "Picking files")
            directory = directoryFromIntent.toString()
        } else {
            Log.v(TAG, "Picking directories")
            directory = ""
        }
        setContentView(R.layout.file_player)
    }

    override fun onStart() {
        super.onStart()

        val recyclerView = findViewById<RecyclerView>(R.id.file_recycler_view)
        val files = File(getExternalFilesDir(null).absolutePath + "/" + directory).
                listFiles {file -> if (TextUtils.isEmpty(directory)) file.isDirectory() else !file.isDirectory}
        val filenames = Array(files.size, {files[it].name})
        recyclerView.adapter = FileNameAdapter(filenames)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun playFile(filename: String) {
        val oldMediaPlayer = mediaPlayer
        if (oldMediaPlayer != null)
            oldMediaPlayer.release()
        val newMediaPlayer = MediaPlayer()
        newMediaPlayer.setDataSource(getExternalFilesDir(null)
                .absolutePath + "/" + directory + "/" + filename)
        newMediaPlayer.prepare()
        newMediaPlayer.start()
        mediaPlayer = newMediaPlayer
    }


    private inner class PlayFileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView;
        init {
            textView = view.findViewById(R.id.fileName)
            if (TextUtils.isEmpty(directory)) {
                textView.setOnClickListener {
                    if (it is TextView) {
                        val intent = Intent(this@FilePlayerActivity, FilePlayerActivity::class.java)
                        intent.putExtra(KEY_DIRECTORY, it.text.toString())
                        startActivity(intent)
                    }
                }
            } else {
                textView.setOnClickListener {
                    if (it is TextView) this@FilePlayerActivity.playFile(it.text.toString())
                }
            }
        }
    }

    private inner class FileNameAdapter : RecyclerView.Adapter<PlayFileViewHolder> {
        val filenames : Array<String>

        constructor(filenames : Array<String>) : super() {
            this.filenames = filenames
        }

        override fun getItemCount(): Int {
            return filenames.size
        }

        override fun onBindViewHolder(holder: PlayFileViewHolder?, position: Int) {
            if (holder != null) holder.textView.text = filenames[position]
        }


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayFileViewHolder {
            return PlayFileViewHolder(LayoutInflater.from(this@FilePlayerActivity).inflate(R.layout.file_name, parent, false))
        }
    }
}
