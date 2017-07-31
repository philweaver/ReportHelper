package com.switchamajig.reporthelper

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import java.security.AccessControlException
import java.util.*

class NoteRecorder constructor(context: Context, readerController: ReaderController) {
    private var mediaRecorder : MediaRecorder? = null
    private val context : Context
    private val readerController : ReaderController

    init {
        this.context = context
        this.readerController = readerController
    }

    fun startRecording() {
        val localMediaRecorder = MediaRecorder()
        mediaRecorder = localMediaRecorder
        localMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        localMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        localMediaRecorder.setOutputFile(createFileName());
        localMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        localMediaRecorder.prepare()
        localMediaRecorder.start()
    }

    fun stopRecording() {
        val localMediaRecorder = mediaRecorder;
        if (localMediaRecorder == null) return;
        localMediaRecorder.stop()
        localMediaRecorder.reset()
        localMediaRecorder.release()
        mediaRecorder = null
    }

    private fun createFileName() : String {
        val calendar = Calendar.getInstance()
        val fullDirectoryPath = context.getExternalFilesDir(null).absolutePath + "/" +
                readerController.bookTitle
        File(fullDirectoryPath).mkdir()
        return fullDirectoryPath + "/" +
                calendar.get(Calendar.YEAR) + "_" +
                (calendar.get(Calendar.MONTH) + 1) + "_" +
                calendar.get(Calendar.DAY_OF_MONTH) + "_" +
                calendar.get(Calendar.HOUR_OF_DAY) + "_" +
                calendar.get(Calendar.MINUTE) + "_" +
                calendar.get(Calendar.SECOND) + ".3gp"
    }
}