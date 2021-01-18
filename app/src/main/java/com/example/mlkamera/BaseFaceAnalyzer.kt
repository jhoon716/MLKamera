package com.example.mlkamera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class BaseFaceAnalyzer<T> : ImageAnalysis.Analyzer {

    abstract val faceState: FaceState

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            detectInImage(InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees))
                    .addOnSuccessListener { results ->
                        onSuccess(
                                results,
                                faceState
                        )
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
        }
    }

    abstract fun stop()

    protected abstract fun detectInImage(image: InputImage): Task<T>

    protected abstract fun onSuccess(
            results: T,
            faceState: FaceState
    )

    protected abstract fun onFailure(e: Exception)
}