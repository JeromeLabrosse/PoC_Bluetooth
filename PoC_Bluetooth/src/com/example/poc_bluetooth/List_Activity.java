package com.example.poc_bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.bluetooth.*;

import java.util.*;


public class List_Activity extends Activity {

  Boolean D = true;
  BluetoothAdapter mBluetoothAdapter = null;
  ListView mListView1 = null;
  ArrayAdapter<String> mAADeviceName = null;
  ArrayAdapter<BluetoothDevice> mAADevice = null;
  TextView mTextView1 = null;

  Intent myIntent = null;

  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      // When discovery finds a device
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        if(D)  Log.d("BLUETOOTH", "Discovering !");
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent
            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        // Add the name and address to an array adapter to show in a ListView
        mAADeviceName.add(device.getName());
        mAADevice.add(device);
        mListView1.setAdapter(mAADeviceName);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mListView1 = (ListView) findViewById(R.id.listview1);
    mAADeviceName = new ArrayAdapter<String>(this,
        R.layout.paired_or_visible_list);
    mAADevice = new ArrayAdapter<BluetoothDevice>(this,
        R.layout.paired_or_visible_list);
    mTextView1 = (TextView) findViewById(R.id.textview1);
    mListView1.setOnItemClickListener(new ListView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> a, View v, int i, long l) {
        if (i < mAADevice.getCount()) {
          BluetoothDevice tempDevice = mAADevice.getItem(i);
          if(D) Log.d("BLUETOOTH", " Selected device : " + tempDevice.getName() + ", add : " + tempDevice.getAddress());
          if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
          }
          myIntent = new Intent(List_Activity.this, BTDeviceActivity.class);
          myIntent.putExtra("key", tempDevice);
          List_Activity.this.startActivity(myIntent);
          
        }
        else {
          if(D) Log.d("BLUETOOTH", " indice i does not exist ");
        }
      }
    });
  }

  /*
   * pairedResearch : Research of all paired devices
   */
  public void pairedResearch(View view) {

    mAADeviceName.clear();
    mAADevice.clear();
    mTextView1.setText("  Liste des appareils appariés :");
    // paired devices
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    // If there are paired devices
    if (pairedDevices.size() > 0) {
      Log.d("BLUETOOTH", "Some devices are paired");
      // Loop through paired devices
      for (BluetoothDevice device : pairedDevices) {
        // Add the name and address to an array adapter to show in a ListView
        Log.d("BLUETOOTH", "PAIRED : name : " + device.getName()
            + ", address : " + device.getAddress());
        mAADeviceName.add(device.getName());
        mAADevice.add(device);
      }
      mListView1.setAdapter(mAADeviceName);
    }

    else {
      Log.d("BLUETOOTH", "No device are paired");
    }

  }

  /*
   * visibleResearch : Research of all visible devices
   */
  public void visibleResearch(View view) {
    mAADeviceName.clear();
    mAADevice.clear();
    mTextView1.setText("  Liste des appareils visibles :");
    // Register the BroadcastReceiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(mReceiver, filter);
    if(mBluetoothAdapter.isDiscovering()){
      mBluetoothAdapter.cancelDiscovery();
    }
    mBluetoothAdapter.startDiscovery();
    Log.d("BLUETOOTH", "Start discovery");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mReceiver != null)
      unregisterReceiver(mReceiver);
  }

}
