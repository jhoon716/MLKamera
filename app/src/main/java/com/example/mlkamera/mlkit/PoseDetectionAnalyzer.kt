package com.example.mlkamera.mlkit

import android.graphics.Rect
import android.util.Log
import com.example.mlkamera.BaseImageAnalyzer
import com.example.mlkamera.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.IOException
import java.lang.Exception

class PoseDetectionAnalyzer(private val view: GraphicOverlay) : BaseImageAnalyzer<Pose>() {

    private val realTimeOpts = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

    private val detector = PoseDetection.getClient(realTimeOpts)
    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<Pose> {
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
        result: Pose,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        val poseGraphic = PoseGraphic(graphicOverlay, result, rect)
        graphicOverlay.add(poseGraphic)
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Pose Detection failed.$e")
    }

    companion object {
        private const val TAG = "PoseAnalyzer"
    }
}