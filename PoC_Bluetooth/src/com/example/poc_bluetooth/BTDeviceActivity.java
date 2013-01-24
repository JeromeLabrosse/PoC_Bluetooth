package com.example.poc_bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.lang.reflect.Method;
import  java.lang.reflect.InvocationTargetException;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.*;

public class BTDeviceActivity extends Activity {

  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private AcceptThread mAcceptThread;
  private ConnectThread mConnectThread;

  private TextView mTextview1 = null;
  private TextView mTextview2 = null;
  private TextView mTextview3 = null;
  private BluetoothDevice mBTDevice = null;

  private BluetoothAdapter mBluetoothAdapter = null;

  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_btdevice);

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // recover BluetoothDevice
    Intent intent = getIntent();
    mBTDevice = (BluetoothDevice) intent.getParcelableExtra("key");

    // create textview and print device information
    mTextview1 = (TextView) findViewById(R.id.textview1);
    mTextview2 = (TextView) findViewById(R.id.textview2);
    mTextview3 = (TextView) findViewById(R.id.textview3);

    mTextview1.setText("Nom : " + mBTDevice.getName());
    mTextview2.setText("Adresse Mac : " + mBTDevice.getAddress());
    mTextview3.setText("Propriété : " + mBTDevice.getBondState());

  }

  public void tryAppaired(View view) {

    if (mConnectThread == null) {
      mConnectThread = new ConnectThread(mBTDevice);
      mConnectThread.start();
    }

    /*requestBTDiscoverable();    
    if (mAcceptThread == null) {
      mAcceptThread = new AcceptThread();
      mAcceptThread.start(); 
    }
     */
  }



  //CLIENT
  
  private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
      // Use a temporary object that is later assigned to mmSocket,
      // because mmSocket is final
      BluetoothSocket tmp = null;
      mmDevice = device;

      // Get a BluetoothSocket to connect with the given BluetoothDevice
      try {
        //
        Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
        tmp = (BluetoothSocket) m.invoke(device, 1);
  
      } catch (IllegalAccessException e) {
        Log.d("BLUETOOTH", "e1 : " + e.getMessage());
      }
      catch (InvocationTargetException e) {
        Log.d("BLUETOOTH", "e2 : " + e.getMessage());
      }
      catch (NoSuchMethodException e) {
        Log.d("BLUETOOTH", "e3 : " + e.getMessage());
      }
      mmSocket = tmp;
    }

    public void run() {
      // Cancel discovery because it will slow down the connection
      mBluetoothAdapter.cancelDiscovery();

      try {
        // Connect the device through the socket. This will block
        // until it succeeds or throws an exception
        mmSocket.connect();
        Intent myIntent = new Intent(BTDeviceActivity.this, ResultActivity.class);
        myIntent.putExtra("key", "Apparié");
        BTDeviceActivity.this.startActivity(myIntent);
        
        
      } catch (IOException connectException) {
        Intent myIntent = new Intent(BTDeviceActivity.this, ResultActivity.class);
        myIntent.putExtra("key", "Echec");
        BTDeviceActivity.this.startActivity(myIntent);
        
        Log.d("BLUETOOTH", "e4 : " + connectException.getMessage());
        

        // Unable to connect; close the socket and get out
        try {
          mmSocket.close();
        } catch (IOException closeException) {
          Log.d("BLUETOOTH", "e5 : " + closeException.getMessage());

        }
        return;
      }

      Log.d("BLUETOOTH", "manageConnectedSocket");
      // Do work to manage the connection (in a separate thread)
      // manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
      }
    }
  }

  
  //SERVER
  private class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
      // Use a temporary object that is later assigned to mmServerSocket,
      // because mmServerSocket is final
      BluetoothServerSocket tmp = null;
      try {
        // MY_UUID is the app's UUID string, also used by the client code
        tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("PoCBluetooth", MY_UUID);
      } catch (IOException e) {
        Log.d("BLUETOOTH", e.getMessage());
      }
      mmServerSocket = tmp;
    }

    public void run() {

      BluetoothSocket socket = null;
      // Keep listening until exception occurs or a socket is returned
      while (true) {
        try {
          Log.d("BLUETOOTH", "wait connection...");
          socket = mmServerSocket.accept();
        } catch (IOException e) {
          Log.d("BLUETOOTH", e.getMessage());
          break;
        }

        // If a connection was accepted
        if (socket != null) {
          Log.d("BLUETOOTH", "Socket connected !");

          // Do work to manage the connection (in a separate thread)
          // manageConnectedSocket(socket);

          try {
            mmServerSocket.close();
          } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
          }

          break;
        }
      }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
      try {
        mmServerSocket.close();
      } catch (IOException e) {
      }
    }
  }
  

  public void requestBTDiscoverable() {
    // If we're already discovering, stop it
    if (mBluetoothAdapter.isDiscovering()) {
      mBluetoothAdapter.cancelDiscovery();
    }

    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

    startActivityForResult(i, 0);

    int result = 0;

    this.onActivityResult(0, result, i);
    Log.i("BLUETOOTH", "Bluetooth discoverability enabled");

  }

}
