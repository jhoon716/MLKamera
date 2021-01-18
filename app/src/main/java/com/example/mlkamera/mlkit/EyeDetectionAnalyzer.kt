package com.example.mlkamera.mlkit

import android.util.Log
import com.example.mlkamera.BaseFaceAnalyzer
import com.example.mlkamera.FaceState
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException
import java.lang.Exception

class EyeDetectionAnalyzer(private var state: FaceState) : BaseFaceAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)
    override val faceState: FaceState
        get() = state

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        faceState: FaceState
    ) {
        results.forEach {
            faceState.setEyesOpen(it.leftEyeOpenProbability, it.rightEyeOpenProbability)
        }
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detection failed.$e")
    }

    companion object {
        private const val TAG = "FaceContourAnalyzer"
    }
}