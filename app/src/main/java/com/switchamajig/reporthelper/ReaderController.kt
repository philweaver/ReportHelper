package com.switchamajig.reporthelper

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo

class ReaderController {
    constructor(service : AccessibilityService) {
        this.service = service
    }
    private var activelyReading = true
    private val service : AccessibilityService

    val reading : Boolean
        get() = activelyReading

    val bookTitle : String
        get() = findCurrentBookTitle()

    // TODO: Deal with reading stopping because the user navigated away from the app
    fun startReading() {
        val playNode = findNodeInWindowList(service.windows, this::isPlay)
        if (playNode != null) {
            playNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            activelyReading = true
        }
    }

    fun stopReading() {
        val pauseNode = findNodeInWindowList(service.windows, this::isPause)
        if (pauseNode != null) {
            pauseNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        activelyReading = false
    }

    private fun isPlay(nodeInfo: AccessibilityNodeInfo) : Boolean {
        return isNodeWithText(nodeInfo, "play")
    }

    private fun isPause(nodeInfo: AccessibilityNodeInfo) : Boolean {
        return isNodeWithText(nodeInfo, "pause")
    }

    private fun isNodeWithText(nodeInfo: AccessibilityNodeInfo, text: String) : Boolean {
        val contentDescription = nodeInfo.contentDescription
        if (contentDescription == null) return false
        return contentDescription.contains(text)
    }

    private fun findNodeInWindowList(windowList: List<AccessibilityWindowInfo>,
                                     predicate: (AccessibilityNodeInfo) -> Boolean) : AccessibilityNodeInfo? {
        for (window in windowList) {
            val root = window.root
            if (root != null) {
                val matchingNode = findNodeInHierarchy(root, predicate)
                if (matchingNode != null) {
                    return matchingNode
                }
                root.recycle()
            }
        }
        return null
    }

    private fun findNodeInHierarchy(nodeInfo: AccessibilityNodeInfo,
                            predicate: (AccessibilityNodeInfo) -> Boolean) : AccessibilityNodeInfo? {
        if (predicate(nodeInfo))
            return nodeInfo
        for (i in 0..(nodeInfo.childCount - 1)) {
            val child = nodeInfo.getChild(i)
            if (child != null) {
                val foundNode = findNodeInHierarchy(child, predicate)
                if (foundNode != null) return foundNode
                child.recycle()
            }
        }
        return null
    }

    private fun findCurrentBookTitle() : String {
        val node = findNodeInWindowList(service.windows, {isTitleNode(it)})
        if (node != null) {
            val fullTitle = node.text
            val shortenedTitle = fullTitle.subSequence(0, Math.min(fullTitle.length, 15)).toString()
            return shortenedTitle.replace("[:]", "")
        }
        return "Unknown"
    }

    private fun isTitleNode(node : AccessibilityNodeInfo) : Boolean {
        if (node.window.type != AccessibilityWindowInfo.TYPE_APPLICATION) return false;
        return node.text != null;
    }
}
