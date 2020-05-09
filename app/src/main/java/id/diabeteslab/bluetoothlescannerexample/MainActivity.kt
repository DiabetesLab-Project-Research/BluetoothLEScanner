package id.diabeteslab.bluetoothlescannerexample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.clj.fastble.BleManager
import id.diabeteslab.bluetoothlescannerexample.bluetooth.BluetoothService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var btnScanBluetooth: Button
    private lateinit var rvBluetooth: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get component instances.
        btnScanBluetooth = btn_scan
        rvBluetooth = rv_bluetooth

        // Setup BleManager.
        BleManager.getInstance().init(this.application)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setConnectOverTime(20000).operateTimeout = 5000

        // Scan bluetooth button.
        btnScanBluetooth.setOnClickListener {
            BluetoothService.runOnPermission(this, rvBluetooth)
        }
    }
}
