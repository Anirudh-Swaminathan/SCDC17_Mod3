package com.example.anicodebreaker.scdc_mod3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anicodebreaker.scdc_mod3.rest.ApiClient;

public class ChangeIp extends AppCompatActivity {

    EditText newIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ip);

        newIp = (EditText) findViewById(R.id.ipInp);

        (findViewById(R.id.chIpBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ip = newIp.getText().toString();
                if (ip.equals("")) {
                    Toast.makeText(ChangeIp.this, "Please enter some IP to change to", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuffer buff = new StringBuffer();
                    buff.append("Do you wish to change the website IP?");

                    AlertDialog.Builder abu = new AlertDialog.Builder(ChangeIp.this);
                    abu.setMessage(buff.toString()).setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ApiClient.changeApiBaseUrl(ip);
                                SharedPreferences sharedPref = getSharedPreferences("netInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("ipAddr", ip);
                                editor.apply();
                            } catch (Exception e) {
                                Toast.makeText(ChangeIp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                finish();
                            }

                            Intent mai = new Intent(ChangeIp.this, MainActivity.class);
                            startActivity(mai);
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = abu.create();
                    alert.setTitle("Confirm Change");
                    alert.show();
                }
            }
        });
    }
}
