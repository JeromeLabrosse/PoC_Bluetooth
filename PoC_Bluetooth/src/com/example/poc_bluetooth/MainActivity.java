package com.example.poc_bluetooth;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.bluetooth.*;
import android.content.DialogInterface;
import android.content.Intent;


public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void testConnectivity(View view) {

    // Bluetooth
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter != null) {
      // Check if the bluetooth is enable
      if (!bluetoothAdapter.isEnabled()) {
        
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Non connecté \nVoulez-vous modifier vos configurations ?")
            .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                MainActivity.this.startActivity(intent);

              }
            }).setNegativeButton("NON", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                Log.d("HTTPS", "NON");
              }
            });
        // Create the AlertDialog object and return it
        alertDialog.create();
        alertDialog.show();
        
      }
      else{
        Intent myIntent = new Intent(MainActivity.this, List_Activity.class);
        MainActivity.this.startActivity(myIntent);
      }
    }
  }

}
