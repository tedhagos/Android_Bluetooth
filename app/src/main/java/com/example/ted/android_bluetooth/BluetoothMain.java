package com.example.ted.android_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class BluetoothMain extends AppCompatActivity {

  final static String logtag = "BlueTooth";

  BluetoothAdapter bta;
  int DISCOVERY_REQUEST = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bluetooth_main);

    bta = BluetoothAdapter.getDefaultAdapter();

    checkStatus();

    Button btnconnect = (Button) findViewById(R.id.btnconnect);
    Button btndisconnect =  (Button) findViewById(R.id.btndisconnect);

    assert btnconnect != null;
    btnconnect.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        connect();
      }
    });

    assert btndisconnect != null;
    btndisconnect.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        disconnect();
      }
    });
   }

  private void checkStatus() {
    String status = null;
    if(bta.isEnabled()) {
      status = String.format("addr: %s\n name: %s", bta.getAddress(), bta.getName());
    }
    else {
      status = "Either there is no bluetooth or it is off";
    }
    Toast.makeText(this, status, Toast.LENGTH_LONG ).show();
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(btstate);
  }

  @Override
  public void onResume() {
    super.onResume();
    //String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
    //String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
    //IntentFilter filter = new IntentFilter(scanModeChanged);
    //registerReceiver(btstate, filter);
    //Log.d(logtag, "onResume");
  }

  private void connect() {
    //startActivityForResult(new Intent(actionRequestEnable), 0);
    //String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;

    String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;

    IntentFilter f1 = new IntentFilter(scanModeChanged);
    IntentFilter f4 = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    registerReceiver(btstate, f1);
    //registerReceiver(btstate, f2);
    registerReceiver(discoveryResult, f4);

    String actionRequestDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
    Intent intent = new Intent(actionRequestDiscoverable);
    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0 );
    startActivityForResult(intent, DISCOVERY_REQUEST);

    Log.d(logtag, "End of connect");

  }

  private void disconnect() {
    bta.disable();
    Log.d(logtag, "disconnected");
  }

  private void listDevices() {

    Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
    for(BluetoothDevice pairedDevice : pairedDevices) {
      Log.d(logtag, String.format("Found : %s", pairedDevice.getName()));
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Log.d(logtag, String.format("resultCode: %d", resultCode));

    if(requestCode == DISCOVERY_REQUEST) {
      Toast.makeText(this, "Discovering ...", Toast.LENGTH_LONG).show();
      Log.d(logtag, "Discovering ...");
      listDevices();
      boolean discover = bta.startDiscovery();
      Log.d(logtag, String.format("Bluetooth startDiscovery = %s", discover+""));
    }
    else {
      Log.d(logtag, "DISCOVERY REQUEST not granted , result code = " + resultCode);
    }

  }

  BroadcastReceiver discoveryResult = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
      BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
      Log.d(logtag, String.format("Discovered : %s", remoteDeviceName));
    }
  };

  BroadcastReceiver btstate = new BroadcastReceiver() {

    final static String logtag = "BlueTooth";

    @Override
    public void onReceive(Context context, Intent intent) {

      Log.d(logtag, "inside onReceive");

      String stateExtra = BluetoothAdapter.EXTRA_STATE;
      int state = intent.getIntExtra(stateExtra, -1);
      String statustext = "";

      switch(state) {
        case BluetoothAdapter.STATE_TURNING_ON:
          statustext = "Turning on";
          Log.d(logtag, statustext);
          break;
        case BluetoothAdapter.STATE_ON:
          statustext = "Bluetooth is on";
          Log.d(logtag, statustext);
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          statustext = "Turning off";
          Log.d(logtag, statustext);
          break;
        case BluetoothAdapter.STATE_OFF:
          statustext = "Bluetooth is off";
          Log.d(logtag, statustext);
          break;
        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
          statustext = "Scan Mode Connectable and discoverable";
          Log.d(logtag, statustext);
          break;
        default:
          statustext = String.format("Unhandled status : %s", state);
      }
      Toast.makeText(context, statustext, Toast.LENGTH_LONG).show();
     }
  };

}
