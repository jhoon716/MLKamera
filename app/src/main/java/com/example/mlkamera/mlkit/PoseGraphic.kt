package com.example.mlkamera.mlkit

import android.graphics.*
import com.example.mlkamera.GraphicOverlay
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

// variables used to count reps
var g_armsUp = false
var g_count = 0

class PoseGraphic(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val rightLandmarkPaint: Paint
    private val leftLandmarkPaint: Paint
    private val linePaint: Paint
    private val textPaint: Paint

    init {
        val selectedColor = Color.WHITE

        rightLandmarkPaint = Paint()
        rightLandmarkPaint.color = Color.GREEN
        leftLandmarkPaint = Paint()
        leftLandmarkPaint.color = Color.RED

        linePaint = Paint()
        linePaint.color = selectedColor
        linePaint.strokeWidth = STROKE_WIDTH

        textPaint = Paint()
        textPaint.color = selectedColor
        textPaint.textSize = TEXT_SIZE
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
            if (it.landmarkType == PoseLandmark.RIGHT_EYE)
                drawPoint(canvas, it, rightLandmarkPaint)
            else if (it.landmarkType == PoseLandmark.LEFT_EYE)
                drawPoint(canvas, it, leftLandmarkPaint)
            else if (it.landmarkType % 2 == 0)
                drawPoint(canvas, it, rightLandmarkPaint)
            else
                drawPoint(canvas, it, leftLandmarkPaint)
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

        drawLine(canvas, leftShoulder!!, rightShoulder!!, linePaint)
        drawLine(canvas, leftHip!!, rightHip!!, linePaint)
        // Left body
        drawLine(canvas, leftShoulder, leftElbow!!, linePaint)
        drawLine(canvas, leftElbow, leftWrist!!, linePaint)
        drawLine(canvas, leftShoulder, leftHip, linePaint)
        drawLine(canvas, leftHip, leftKnee!!, linePaint)
        drawLine(canvas, leftKnee, leftAnkle!!, linePaint)
        drawLine(canvas, leftWrist, leftThumb!!, linePaint)
        drawLine(canvas, leftWrist, leftPinky!!, linePaint)
        drawLine(canvas, leftWrist, leftIndex!!, linePaint)
        drawLine(canvas, leftAnkle, leftHeel!!, linePaint)
        drawLine(canvas, leftHeel, leftFootIndex!!, linePaint)
        // Right body
        drawLine(canvas, rightShoulder, rightElbow!!, linePaint)
        drawLine(canvas, rightElbow, rightWrist!!, linePaint)
        drawLine(canvas, rightShoulder, rightHip, linePaint)
        drawLine(canvas, rightHip, rightKnee!!, linePaint)
        drawLine(canvas, rightKnee, rightAnkle!!, linePaint)
        drawLine(canvas, rightWrist, rightThumb!!, linePaint)
        drawLine(canvas, rightWrist, rightPinky!!, linePaint)
        drawLine(canvas, rightWrist, rightIndex!!, linePaint)
        drawLine(canvas, rightAnkle, rightHeel!!, linePaint)
        drawLine(canvas, rightHeel, rightFootIndex!!, linePaint)

        // Angles of elbows and knees
//        drawAngle(canvas, leftShoulder, leftElbow, leftWrist, textPaint)
//        drawAngle(canvas, rightShoulder, rightElbow, rightWrist, textPaint)
//        drawAngle(canvas, leftHip, leftKnee, leftAnkle, textPaint)
//        drawAngle(canvas, leftHip, rightKnee, rightAnkle, textPaint)

        // Count squats
        countJumpingJacks(canvas, leftShoulder, leftElbow, leftHip, rightShoulder, rightElbow, rightHip, textPaint)
//        countSquats(canvas, rightShoulder, rightHip, rightKnee, rightAnkle, textPaint)
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

    private fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
                (atan2(lastPoint.position.y - midPoint.position.y,
                        lastPoint.position.x - midPoint.position.x)
                        - atan2(firstPoint.position.y - midPoint.position.y,
                        firstPoint.position.x - midPoint.position.x)).toDouble())
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    private fun drawAngle(
            canvas: Canvas?,
            firstPoint: PoseLandmark
            , midPoint: PoseLandmark,
            lastPoint: PoseLandmark,
            paint: Paint) {
        var angle = String.format("%.2f", getAngle(firstPoint, midPoint, lastPoint))
        canvas?.drawText(
                angle,
                translateX(midPoint.position.x),
                translateY(midPoint.position.y),
                paint)
    }

    private fun countJumpingJacks(
            canvas: Canvas?,
            leftShoulder: PoseLandmark,
            leftElbow: PoseLandmark,
            leftHip: PoseLandmark,
            rightShoulder: PoseLandmark,
            rightElbow: PoseLandmark,
            rightHip: PoseLandmark,
            paint: Paint) {
        val leftShoulderAngle = getAngle(leftElbow, leftShoulder, leftHip)
        val rightShoulderAngle = getAngle(rightElbow, rightShoulder, rightHip)
        if (leftShoulderAngle >= 90 && rightShoulderAngle >= 90) {
            g_armsUp = true
        } else if (leftShoulderAngle < 90 && rightShoulderAngle < 90 && g_armsUp) {
            g_count += 1
            g_armsUp = false
        }
        canvas?.drawText(g_count.toString(), 10f, 200f, paint)
    }

    companion object {
        private const val POSE_POSITION_RADIUS = 8.0F
        private const val TEXT_SIZE = 120.0F
        private const val STROKE_WIDTH = 5.0F
    }
}