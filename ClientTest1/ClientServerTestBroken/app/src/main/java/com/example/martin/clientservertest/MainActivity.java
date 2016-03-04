package com.example.martin.clientservertest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.*;

public class MainActivity extends Activity {

    TextView response, debugMsg;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear, buttonDisconnect;
    Client myClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.pordEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        buttonDisconnect = (Button) findViewById(R.id.disconnectButton);
        response = (TextView)findViewById(R.id.responseTextView);
        debugMsg = (TextView)findViewById(R.id.debugText);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(myClient == null)
                {
                    myClient = new Client(editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClient.start();
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myClient == null)
                {
                    return;
                }
                while(myClient.isAlive())
                {
                    if(myClient != null && !myClient.isSocketClosed())
                    {
                        myClient.disconnect();
                    }
                }
                myClient = null;
            }

        });

    }
}
