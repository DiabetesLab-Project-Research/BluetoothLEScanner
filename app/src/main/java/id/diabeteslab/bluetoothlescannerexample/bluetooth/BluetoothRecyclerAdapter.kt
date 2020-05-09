package id.diabeteslab.bluetoothlescannerexample.bluetooth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clj.fastble.data.BleDevice
import id.diabeteslab.bluetoothlescannerexample.R
import kotlinx.android.synthetic.main.item_bluetooth.view.*

class BluetoothRecyclerAdapter(private var items: MutableList<BleDevice>) :
    RecyclerView.Adapter<BluetoothViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_bluetooth, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.bindItem(items[position])
    }
}

class BluetoothViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val root = view
    private val tvDeviceName = root.device_name
    private val tvMacAddress = root.mac_address

    fun bindItem(item: BleDevice) {
        tvDeviceName.text = item.name
        tvMacAddress.text = item.mac
    }
}