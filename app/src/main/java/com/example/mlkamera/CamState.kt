package com.example.mlkamera

class CamState {
    /*
    SmileState: true if Smile Mode is on
    BlinkState: true if Blink Mode is on
    ModeState: Checks whether eyes are closed are not in BlinkState
                                smiling or not in Smile Mode
                                MODE_OFF when the mode is off
                                REFER TO updateMainView in MainActivity.java
    AutoSmile/Blink: Auto Mode can only be on while Smile/Blink Mode is on (true). It goes to 'false' on...
                                                                    Mode Change (Smile/Blink)
                                                                    User Selection (setting button click)
    PhotoCondition: Default Value: false
                    In Auto Mode && Blink Mode,
                    goes true: at the moment the user pressed the camera button when he/she was closing eyes
                                meaning that now they have a task to take a photo automatically once the user opens his/her eyes.
                    goes false: 1. when the app takes a photo of the user as soon as the user opens eyes/smiles.
                                2. Turns Off the mode
                                3. Changes the type of camera (front/back)
    */

    private var SmileState = false //true if Smile Mode on

    private var BlinkState = false //true if Blink Mode on

    private var ModeState = "MODE_OFF"
    private val AutoSmile = true
    private val AutoBlink = true
    private var PhotoCondition = false

    // SmileState
    fun getSmileState(): Boolean? {
        return SmileState
    }

    fun setSmileState(smileState: Boolean) {
        SmileState = smileState
    }

    // BlinkState
    fun getBlinkState(): Boolean? {
        return BlinkState
    }

    fun setBlinkState(blinkState: Boolean) {
        BlinkState = blinkState
    }

    // CameraMode
    fun getCameraMode(): String? {
        return ModeState
    }

    fun setCameraMode(modeState: String) {
        ModeState = modeState
    }

    // Auto Modes
    fun SmileAutoMode(): Boolean {
        return AutoSmile
    }

    fun BlinkAutoMode(): Boolean {
        return AutoBlink
    }

    // Photo Condition
    fun getPhotoCondition(): Boolean {
        return PhotoCondition
    }

    fun setPhotoCondition(photoCondition: Boolean) {
        PhotoCondition = photoCondition
    }
}