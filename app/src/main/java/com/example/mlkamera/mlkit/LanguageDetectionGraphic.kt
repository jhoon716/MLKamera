package com.example.mlkamera.mlkit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.example.mlkamera.GraphicOverlay
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.vision.text.Text

class LanguageDetectionGraphic(
    overlay: GraphicOverlay,
    private val element: Text.TextBlock,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint = Paint().apply {
        color =
            RECT_COLOR
        style = Paint.Style.FILL
        alpha = 50
    }

    private val textPaint = Paint().apply {
        color =
            TEXT_COLOR
        textSize =
            TEXT_SIZE
    }

    override fun draw(canvas: Canvas?) {
        element.boundingBox?.let { box ->
            val rect = calculateRect(imageRect.height().toFloat(), imageRect.width().toFloat(), box)
            canvas?.drawRoundRect(rect,
                ROUND_RECT_CORNER,
                ROUND_RECT_CORNER, rectPaint)
            val languageIdentifier = LanguageIdentification.getClient()
            languageIdentifier.identifyLanguage(element.text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == "und") {
                        Log.i(TAG, "Can't identify language")
                    } else {
                        canvas?.drawText(languageCode, rect.centerX(), rect.centerY(), textPaint)
                    }
                }
        }
    }

    companion object {
        private const val TEXT_COLOR = Color.WHITE
        private const val TEXT_SIZE = 150.0f
        private const val RECT_COLOR = Color.BLACK
        private const val ROUND_RECT_CORNER = 8F
        private const val TAG = "LanguageDetection"
    }
}