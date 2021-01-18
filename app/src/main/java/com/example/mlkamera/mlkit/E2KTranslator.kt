package com.example.mlkamera.mlkit

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.Text

class E2KTranslator : Translator {

    private var englishKoreanTranslator: Translator

    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()
        englishKoreanTranslator = Translation.getClient(options)

        downloadModelIfNeeded()
    }

    override fun downloadModelIfNeeded(): Task<Void> {
        // Download language model
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        return englishKoreanTranslator.downloadModelIfNeeded(conditions)
            .addOnCompleteListener {
                Log.i(TAG, "Successfully downloaded EN-KO language package.")
            }
            .addOnFailureListener { exc ->
                Log.w(TAG, "Language download failed.$exc")
            }
    }

    override fun downloadModelIfNeeded(p0: DownloadConditions): Task<Void> {
        return englishKoreanTranslator.downloadModelIfNeeded(p0)
            .addOnCompleteListener {
                Log.i(TAG, "Successfully downloaded EN-KO language package.")
            }
            .addOnFailureListener { exc ->
                Log.w(TAG, "Language download failed.$exc")
            }
    }

    override fun translate(p0: String): Task<String> {
        return englishKoreanTranslator.translate(p0)
    }

    override fun close() {
        englishKoreanTranslator.close()
    }

    companion object {
        private const val TAG = "Translator"
    }
}