package com.switchamajig.reporthelper

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class ReportHelperAccessibilityService : AccessibilityService() {
    private val readerController = ReaderController(this)
    private val noteRecorder = NoteRecorder(this, readerController)
    private lateinit var recordingIndicator: RecordingIndicator

    override fun onServiceConnected() {
        super.onServiceConnected()
        recordingIndicator = RecordingIndicator(this)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            toggleReading();
        }
        return true
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun toggleReading() {
        if (readerController.reading) {
            readerController.stopReading()
            noteRecorder.startRecording()
            recordingIndicator.visible = true
        } else {
            readerController.startReading()
            noteRecorder.stopRecording()
            recordingIndicator.visible = false
        }
    }
}