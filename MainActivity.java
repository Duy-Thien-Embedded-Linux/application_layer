package com.example.ledcontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.startandbound_001.ILedServiceManager;


public class MainActivity extends AppCompatActivity {
    Button mGetButton;
    Button mSetButton;
    EditText mInput;
    TextView mOutput;
    int ledState = 0;
    ILedServiceManager mLedService;
    private final ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLedService = ILedServiceManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLedService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = new Intent("ILedServiceManager");
        intent.setPackage("com.example.startandbound_001");
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        mGetButton = findViewById(R.id.getButton);
        mSetButton = findViewById(R.id.setButton);
        mInput = findViewById(R.id.inputState);
        mOutput = findViewById(R.id.outputState);

        mGetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int result = mLedService.getLedStatus();
                        } catch (RemoteException e) {
                            Log.d("ledApplication", "Can't access remote object");
                        }
                        mOutput.setText(String.valueOf(ledState));
                        Log.d("ledApplication", "Get Led State");
                    }
                }
        );

        mSetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ledState = Integer.parseInt(String.valueOf(mInput.getText()));
                        try {
                            int result = mLedService.setLedStatus(ledState);
                        } catch (RemoteException e) {
                            Log.d("ledApplication", "Can't access remote object");
                        }
                        Log.d("ledApplication", "Set Led State to: " + ledState);
                    }
                }
        );
    }



}
