package com.bg.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bg.bgpad.R;

import java.util.ArrayList;

/**
 * Created by zjy on 2017-03-02.
 */


public class LeDeviceListAdapter extends BaseAdapter {
    public ArrayList<BluetoothDevice> mLeDevices;
    private Context context;
    private String deviceName;

    public LeDeviceListAdapter(Context context, String deviceName) {
        super();
        this.context = context;
        this.deviceName = deviceName;
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device) && device.getName().equals(deviceName)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view
                    .findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view
                    .findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }


    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
