package com.example.mlkamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mlkamera.mlkit.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
//        shutter_button.setOnClickListener {takePhoto()}

        flip_button.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            graphicOverlay_finder.toggleSelector()

            bindCameraUseCases()
        }

        modeSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                face_camera.id -> imageAnalyzer?.setAnalyzer(cameraExecutor, FaceDetectionAnalyzer(graphicOverlay_finder))
                pose_camera.id -> {
                    g_count = 0
                    imageAnalyzer?.setAnalyzer(cameraExecutor, PoseDetectionAnalyzer(graphicOverlay_finder))
                }
                language_camera.id -> imageAnalyzer?.setAnalyzer(cameraExecutor, LanguageDetectionAnalyzer(graphicOverlay_finder))
                translation_camera.id -> imageAnalyzer?.setAnalyzer(cameraExecutor, TranslationAnalyzer(graphicOverlay_finder))
            }
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.surfaceProvider)
                    }

            imageCapture = ImageCapture.Builder()
                    .build()

            imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutor,
                            FaceDetectionAnalyzer(graphicOverlay_finder)
                        )
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            setUpPinchToZoom()

            // Unbind use cases before rebinding
            cameraProvider?.unbindAll()

            try {
                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "MLKamera"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .build()

        imageCapture = ImageCapture.Builder()
            .build()

//        imageAnalyzer = ImageAnalysis.Builder()
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//            .also {
//                it.setAnalyzer(
//                    cameraExecutor,
//                    PoseDetectionAnalyzer(graphicOverlay_finder)
//                )
//            }
        setUpPinchToZoom()

        cameraProvider?.unbindAll()

        try {
            camera = cameraProvider?.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer)

            preview?.setSurfaceProvider(viewFinder.surfaceProvider)

        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpPinchToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(this, listener)
        viewFinder.setOnTouchListener { _, event ->
            viewFinder.post {
                scaleGestureDetector.onTouchEvent(event)
            }
            return@setOnTouchListener true
        }
    }
}