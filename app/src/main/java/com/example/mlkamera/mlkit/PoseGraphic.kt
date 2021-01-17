package com.example.mlkamera.mlkit

import android.graphics.*
import com.example.mlkamera.GraphicOverlay
import com.example.mlkamera.MainActivity
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PoseGraphic(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val landmarkPositionPaint: Paint
    private val landmarkPositionPaint2: Paint
    private val idPaint: Paint
    private val boxPaint: Paint
    private val leftPaint: Paint
    private val rightPaint: Paint

    init {
        val selectedColor = Color.WHITE

        landmarkPositionPaint = Paint()
//        landmarkPositionPaint.color = selectedColor
        landmarkPositionPaint.color = Color.GREEN
        landmarkPositionPaint2 = Paint()
        landmarkPositionPaint2.color = Color.RED

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = PoseGraphic.BOX_STROKE_WIDTH

        leftPaint = Paint()
//        leftPaint.color = Color.GREEN
        leftPaint.color = Color.WHITE
        leftPaint.strokeWidth = BOX_STROKE_WIDTH
        rightPaint = Paint()
        rightPaint.color = Color.YELLOW
        rightPaint.color = Color.WHITE
        rightPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    override fun draw(canvas: Canvas?) {

        calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            imageRect
        )

        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }

        landmarks.forEach {
//            val px = translateX(it.position.x)
//            val py = translateY(it.position.y)
            if (it.landmarkType % 2 == 0)
                drawPoint(canvas, it, landmarkPositionPaint)
            else
                drawPoint(canvas, it, landmarkPositionPaint2)
//            canvas?.drawCircle(px, py, POSE_POSITION_RADIUS, landmarkPositionPaint)
        }

        val leftShoulder =
            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder =
            pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow =
            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow =
            pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist =
            pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist =
            pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip =
            pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip =
            pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee =
            pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee =
            pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle =
            pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle =
            pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        val leftPinky =
            pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val rightPinky =
            pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val leftIndex =
            pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val rightIndex =
            pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val leftThumb =
            pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val rightThumb =
            pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val leftHeel =
            pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHeel =
            pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftFootIndex =
            pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val rightFootIndex =
            pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

        drawLine(canvas, leftShoulder!!, rightShoulder!!, landmarkPositionPaint)
        drawLine(canvas, leftHip!!, rightHip!!, landmarkPositionPaint)
        // Left body
        drawLine(canvas, leftShoulder, leftElbow!!, leftPaint)
        drawLine(canvas, leftElbow, leftWrist!!, leftPaint)
        drawLine(canvas, leftShoulder, leftHip, leftPaint)
        drawLine(canvas, leftHip, leftKnee!!, leftPaint)
        drawLine(canvas, leftKnee, leftAnkle!!, leftPaint)
        drawLine(canvas, leftWrist, leftThumb!!, leftPaint)
        drawLine(canvas, leftWrist, leftPinky!!, leftPaint)
        drawLine(canvas, leftWrist, leftIndex!!, leftPaint)
        drawLine(canvas, leftAnkle, leftHeel!!, leftPaint)
        drawLine(canvas, leftHeel, leftFootIndex!!, leftPaint)
        // Right body
        drawLine(canvas, rightShoulder, rightElbow!!, rightPaint)
        drawLine(canvas, rightElbow, rightWrist!!, rightPaint)
        drawLine(canvas, rightShoulder, rightHip, rightPaint)
        drawLine(canvas, rightHip, rightKnee!!, rightPaint)
        drawLine(canvas, rightKnee, rightAnkle!!, rightPaint)
        drawLine(canvas, rightWrist, rightThumb!!, rightPaint)
        drawLine(canvas, rightWrist, rightPinky!!, rightPaint)
        drawLine(canvas, rightWrist, rightIndex!!, rightPaint)
        drawLine(canvas, rightAnkle, rightHeel!!, rightPaint)
        drawLine(canvas, rightHeel, rightFootIndex!!, rightPaint)
    }

    private fun drawPoint(canvas: Canvas?, landmark: PoseLandmark, paint: Paint) {
        val point = landmark.position
        canvas?.drawCircle(
            translateX(point.x),
            translateY(point.y),
            POSE_POSITION_RADIUS,
            paint
        )
    }

    private fun drawLine(
        canvas: Canvas?,
        startLandmark: PoseLandmark,
        endLandmark: PoseLandmark,
        paint: Paint
    ) {
        try {
            val start = startLandmark.position
            val end = endLandmark.position
            canvas?.drawLine(
                translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint
            )
        } catch (e: NullPointerException) {}
    }

    companion object {
        private const val POSE_POSITION_RADIUS = 8.0F
        private const val ID_TEXT_SIZE = 30.0F
        private const val BOX_STROKE_WIDTH = 5.0F
    }
}