package com.example.mlkamera

class FaceState {

    private var eyesOpen = false //true if Blink Mode on
    private var smiling = false //true if Smile Mode on

    private var EYES_THRESHOLD = 0.75f
    private var SMILE_THRESHOLD = 0.75f

    // Getters
    public fun getEyesOpen(): Boolean {
        return eyesOpen
    }

    public fun getSmiling(): Boolean {
        return smiling
    }

    public fun getEyesThreshold(): Float {
        return EYES_THRESHOLD
    }

    public fun getSmileThreshold(): Float {
        return SMILE_THRESHOLD
    }

    // Setters
    public fun setSmiling(smiling: Boolean) {
        this.smiling = smiling
    }

    public fun setEyesOpen(leftEyeOpenPossibility: Float, rightEyesOpenPoissibility: Float) {
        eyesOpen = leftEyeOpenPossibility > EYES_THRESHOLD || rightEyesOpenPoissibility > EYES_THRESHOLD
    }

    public fun setSmileThreshold(threshold: Float) {
        this.SMILE_THRESHOLD = threshold
    }

    public fun setEyesThreshold(threshold: Float) {
        this.EYES_THRESHOLD = threshold
    }
}