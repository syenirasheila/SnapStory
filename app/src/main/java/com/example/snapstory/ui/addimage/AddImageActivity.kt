package com.example.snapstory.ui.addimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.snapstory.R
import com.example.snapstory.databinding.ActivityAddImageBinding
import com.example.snapstory.helper.createCustomTempFile
import com.example.snapstory.ui.poststory.PostStoryActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddImageBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationEnabled: Boolean = false
    private var currentLocation: Location? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permissions ->
        permissions.entries.forEach {
            val permission = it.key
            val granted = it.value

            when (permission) {
                REQUIRED_FINE_LOCATION_PERMISSION, REQUIRED_COARSE_LOCATION_PERMISSION -> {
                    if (granted) {
                        showToast(getString(R.string.permissions_granted))
                        enableLocation()
                    } else {
                        showToast(getString(R.string.permissions_denied))
                        locationEnabled = false
                        binding.btnLocationPermission.setImageResource(R.drawable.ic_map_slash_add)
                    }
                }
                REQUIRED_CAMERA_PERMISSION -> {
                    if (granted) {
                        showToast(getString(R.string.permissions_granted))
                    } else {
                        showToast(getString(R.string.permissions_denied))
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private fun allLocationPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, REQUIRED_COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(arrayOf(REQUIRED_CAMERA_PERMISSION))
        }

        with(binding) {
            btnSwitch.setOnClickListener {
                switchCamera()
            }
            btnClose.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            btnCapture.setOnClickListener {
                captureImage()
            }
            btnGallery.setOnClickListener {
                openGallery()
            }
            btnLocationPermission.setOnClickListener {
                toggleLocation()
            }

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        openCamera()
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        openCamera()
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                showToast(getString(R.string.failed_to_start_camera))
                Log.e(TAG, "Failed to start camera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        val photoFile = createCustomTempFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val intent = Intent(this@AddImageActivity, PostStoryActivity::class.java).apply {
                    putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                    currentLocation?.let {
                        putExtra(EXTRA_LOCATION_LAT, it.latitude)
                        putExtra(EXTRA_LOCATION_LON, it.longitude)
                    }
                }
                startActivity(intent)
            }

            override fun onError(exc: ImageCaptureException) {
                showToast(getString(R.string.failed_to_take_photo))
                Log.e(TAG, "Failed to take photo: ${exc.message}")
            }
        })
    }


    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            val intent = Intent(this@AddImageActivity, PostStoryActivity::class.java).apply {
                putExtra(EXTRA_CAMERAX_IMAGE, it.toString())
            }
            startActivity(intent)
        } ?: Log.d("Photo Picker", "No media selected")
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                imageCapture?.targetRotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    private fun toggleLocation() {
        if (locationEnabled) {
            locationEnabled = false
            binding.btnLocationPermission.setImageResource(R.drawable.ic_map_slash_add)
            currentLocation = null
            showToast(getString(R.string.location_disabled))
        } else {
            if (allLocationPermissionsGranted()) {
                enableLocation()
            } else {
                requestLocationPermissions()
            }
        }
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(REQUIRED_FINE_LOCATION_PERMISSION, REQUIRED_COARSE_LOCATION_PERMISSION)
        )
    }

    private fun enableLocation() {
        locationEnabled = true
        binding.btnLocationPermission.setImageResource(R.drawable.ic_map_add)
        showToast(getString(R.string.location_enabled))
        if (ActivityCompat.checkSelfPermission(
                this,
                REQUIRED_FINE_LOCATION_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                REQUIRED_COARSE_LOCATION_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val EXTRA_LOCATION_LAT = "Location Latitude"
        const val EXTRA_LOCATION_LON = "Location Longitude"
        const val CAMERAX_RESULT = 200
        private const val REQUIRED_CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val REQUIRED_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

    }
}
