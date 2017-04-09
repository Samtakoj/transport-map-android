package com.beyondar.android.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.os.Handler
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import android.support.v4.app.Fragment
import com.beyondar.android.util.CompareSizesByArea
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.params.RggbChannelVector
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Range


/**
 * Created by artsiom.chuiko on 09/04/2017.
 * This is a basic camera view to support the android camera2 api
 */
class Camera2View(fragment: Fragment): AutoFitTextureView(fragment.activity) {

    companion object {
        val TAG = "BeyondAR/Camera2View"
        private val MAX_PREVIEW_WIDTH = 1920
        private val MAX_PREVIEW_HEIGHT = 1080
    }

    private val activity: Activity = fragment.activity
    private lateinit var cameraId: String
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var previewSize: Size
    private var sensorOrientation: Int = 0
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread
    private val cameraOpenCloseLock = Semaphore(1)

    private val textureListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }
    }

    private val stateCallback: CameraDevice.StateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice = camera
            Log.e(TAG, "Camera is opened")
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice?) {
            cameraOpenCloseLock.release()
            camera?.close()
            Log.e(TAG, "Camera is closed")
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            cameraOpenCloseLock.release()
            camera?.close()
        }

    }

    fun onResume() {
        startBackgroundThread()
        if (isAvailable) {
            openCamera(width, height)
        } else {
            surfaceTextureListener = textureListener
        }
    }

    fun onPause() {
        closeCamera()
        stopBackgroundThread()
    }

    private fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        setUpCameraOutputs(width, height)
        configureTransform(width, height)
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = this.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    if (null == cameraDevice) {
                        return
                    }
                    this@Camera2View.cameraCaptureSession = cameraCaptureSession
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                    cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler)
                    Log.e(TAG, "Camera preview is created, HW: $isHardwareAccelerated")
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    Toast.makeText(context, "Configuration failed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            cameraCaptureSession.close()
            cameraDevice.close()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("Camera Background")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            manager.cameraIdList.filter {
                val facing = manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING)
                facing == null || facing != CameraCharacteristics.LENS_FACING_FRONT
            }.forEach { cameraId ->
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                // For still image captures, we use the largest available size.
                val largest =  map.getOutputSizes(ImageFormat.JPEG).maxWith(CompareSizesByArea())

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity.windowManager.defaultDisplay.rotation
                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0, Surface.ROTATION_180 -> if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90, Surface.ROTATION_270 -> if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swappedDimensions = true
                    }
                    else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
                }

                val displaySize = Point()
                activity.windowManager.defaultDisplay.getSize(displaySize)
                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y

                if (swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest)

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setAspectRatio(previewSize.width, previewSize.height)
                } else {
                    setAspectRatio(previewSize.height, previewSize.width)
                }

                this@Camera2View.cameraId = cameraId
                Log.e(TAG, "Outputs are ready fro $cameraId camera with ${previewSize.width}x${previewSize.height}")
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int,
                                  maxWidth: Int, maxHeight: Int, aspectRatio: Size?): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = arrayListOf<Size>()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough = arrayListOf<Size>()
        val w = aspectRatio?.width?: 1
        val h = aspectRatio?.height?: 1
        choices.filter {
            it.width <= maxWidth && it.height <= maxHeight && it.height == it.width * h / w
        }.forEach {
            if (it.width >= textureViewWidth && it.height >= textureViewHeight) {
                bigEnough.add(it)
            } else {
                notBigEnough.add(it)
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size > 0) {
            return Collections.min(bigEnough, CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            return Collections.max(notBigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            return choices[0]
        }
    }

    private fun  configureTransform(width: Int, height: Int) {
        if (null == previewSize || null == activity) {
            return
        }
        val rotation = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                    height.toFloat() / previewSize.height,
                    width.toFloat() / previewSize.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation.toFloat() - 2), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        setTransform(matrix)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
    }
}