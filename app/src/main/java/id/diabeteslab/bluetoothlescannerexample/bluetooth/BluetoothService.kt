package id.diabeteslab.bluetoothlescannerexample.bluetooth

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice

class BluetoothService(private val activity: Activity, private val rvBluetooth: RecyclerView) {
    companion object {
        const val REQUEST_CODE_PERMISSION_LOCATION = 2
        const val REQUEST_CODE_OPEN_GPS = 1

        fun runOnPermission(activity: Activity, rvBluetooth: RecyclerView) =
            BluetoothService(activity, rvBluetooth).runOnPermission()
    }

    private fun runOnPermission() {
        // Initialize bluetooth adapter.
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Check if bluetooth adapter is already enabled.
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(activity, "Please turn on Bluetooth first!", Toast.LENGTH_SHORT).show()
            return
        }

        // Check bluetooth permission.
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionDeniedList = ArrayList<String>()
        for (permission in permissions) {
            val permissionCheck = activity.let {
                ContextCompat.checkSelfPermission(it, permission)
            }

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }

        // Request permission if it doesn't already exist.
        if (permissionDeniedList.isNotEmpty()) {
            val deniedPermission = permissionDeniedList.toTypedArray()
            activity.let {
                ActivityCompat.requestPermissions(
                    it,
                    deniedPermission,
                    REQUEST_CODE_PERMISSION_LOCATION
                )
            }
        }
    }

    // Check GPS.
    private fun checkGPSIsOpen(): Boolean {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return false
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }

    private fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                AlertDialog.Builder(activity)
                    .setTitle("Notification")
                    .setMessage("BLE needs to open the positioning function.")
                    .setNegativeButton(
                        activity.getString(android.R.string.cancel)
                    ) { _, _ -> activity.finish() }
                    .setPositiveButton(
                        activity.getString(android.R.string.ok)
                    ) { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_GPS)
                    }
                    .setCancelable(false)
                    .show()
            } else {
                startScan()
            }
        }
    }

    private fun startScan() {
        // Initialize bluetooth devices list.
        val bleDeviceList: MutableList<BleDevice> = mutableListOf()

        // Initialize bluetooth recyclerview adapter.
        val bluetoothRecyclerAdapter = BluetoothRecyclerAdapter(bleDeviceList)

        // Setup bluetooth recyclerview adapter.
        rvBluetooth.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = bluetoothRecyclerAdapter
        }

        // Start bluetooth scanning.
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                // Executed when scanning process is finished.
                // Toast message "Bluetooth scanning finished".
                Toast.makeText(activity, "Bluetooth scanning finished", Toast.LENGTH_SHORT).show()
            }

            override fun onScanStarted(success: Boolean) {
                // Executed when scanning process is started.
                // Toast message "Bluetooth scanning is staerted".
                Toast.makeText(activity, "Bluetooth scanning started.", Toast.LENGTH_SHORT).show()
            }

            override fun onLeScan(bleDevice: BleDevice?) {
                super.onLeScan(bleDevice)
            }

            override fun onScanning(bleDevice: BleDevice) {
                // Executed during scanning process.
                // Add for every bluetooth device founded into the list.
                bleDeviceList.add(bleDevice)
                bluetoothRecyclerAdapter.notifyDataSetChanged()
            }
        })
    }
}