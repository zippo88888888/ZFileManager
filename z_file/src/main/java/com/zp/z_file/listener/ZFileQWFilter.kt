package com.zp.z_file.listener

import com.zp.z_file.content.*
import java.io.File
import java.io.FileFilter

internal class ZFileQWFilter(private var filterArray: Array<String>, private var isOther: Boolean) :
    FileFilter {

    override fun accept(file: File): Boolean {
        if (isOther) {
            if (acceptOther(file.name)) {
                return true
            }
        } else {
            filterArray.forEach {
                if (file.name.accept(it)) {
                    return true
                }
            }
        }
        return false
    }

    private fun acceptOther(name: String): Boolean {
        val isPNG = name.accept(PNG)
        val isJPG = name.accept(JPG)
        val isJPEG = name.accept(JPEG)
        val isGIF = name.accept(GIF)
        val isMP4 = name.accept(MP4)
        val is3GP = name.accept(_3GP)
        val isTXT = name.accept(TXT)
        val isXML = name.accept(XML)
        val isJSON = name.accept(JSON)
        val isDOC = name.accept(DOC)
        val isXLS = name.accept(XLS)
        val isPPT = name.accept(PPT)
        val isPDF = name.accept(PDF)
        return !isPNG && !isJPG && !isJPEG && !isGIF && !isMP4 && !is3GP && !isTXT && !isXML &&
                !isJSON && !isDOC && !isXLS && !isPPT && !isPDF
    }
}