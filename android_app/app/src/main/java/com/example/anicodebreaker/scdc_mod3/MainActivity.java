package com.example.anicodebreaker.scdc_mod3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.anicodebreaker.scdc_mod3.config.Config;
import com.example.anicodebreaker.scdc_mod3.adapters.CustomGridAdapter;
import com.example.anicodebreaker.scdc_mod3.model.HealthData;
import com.example.anicodebreaker.scdc_mod3.rest.ApiClient;
import com.example.anicodebreaker.scdc_mod3.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    // Tag for debugging
    private static String TAG = "MainActivity";

    String ip;
    String glu, temp, hrt, bp;

    GridView aniGrid;

    List<String> data = new ArrayList<>();

    String[] dataGrid;
    private static final String[] titleGrid = {"Sugar Level", "Body Temperature", "Heart Rate", "Blood Pressure"};

    ApiInterface apiService;

    private static String KEY = Config.KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("netInfo", Context.MODE_PRIVATE);
        ip = sharedPref.getString("ipAddr", null);

        if(ip!=null) {
            try {
                ApiClient.changeApiBaseUrl(ip);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // API Calls
        apiService = ApiClient.getClient().create(ApiInterface.class);

        aniGrid = (GridView) findViewById(R.id.dataGrid);

        glu = sharedPref.getString("glu", null);
        temp = sharedPref.getString("temp", null);
        hrt = sharedPref.getString("hrt", null);
        bp = sharedPref.getString("bp", null);

        if(ip == null) {
            Toast.makeText(MainActivity.this, "Please choose the website for now",Toast.LENGTH_SHORT).show();
            Intent fill = new Intent(MainActivity.this, ChangeIp.class);
            startActivity(fill);
            finish();
        } else {
            if(glu==null || temp==null || hrt==null || bp==null){
                getData();
            } else {
                data.clear();
                data.add(glu); data.add(temp); data.add(hrt); data.add(bp);
            }
        }
        dataGrid = data.toArray(new String[0]);
        if(dataGrid.length != 0) aniGrid.setAdapter(new CustomGridAdapter(MainActivity.this, titleGrid, dataGrid));

        (findViewById(R.id.refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        (findViewById(R.id.diagBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag();
            }
        });

        aniGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomGridAdapter.ViewHolder holder = (CustomGridAdapter.ViewHolder) view.getTag();
                String v = holder.d.getText().toString();
                showGridDiag(position, v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.chIpMen:
                Intent addM = new Intent(MainActivity.this, ChangeIp.class);
                startActivity(addM);
                break;
            default:
                Toast.makeText(MainActivity.this, "You made the wrong choice", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * function to retrieve encrypted data from the API
     */
    private void getData(){
        Call<HealthData> call = apiService.getLatestData();
        call.enqueue(new Callback<HealthData>() {
            @Override
            public void onResponse(Call<HealthData> call, Response<HealthData> response) {
                try {
                    Log.wtf(TAG, "onResponse");
                    int res = response.body().getMsg();
                    Log.wtf(TAG, "Status is: "+ Integer.toString(res));
                    if (res == 200) {
                        String ret = response.body().getRet();

                        // Decrypt response
                        String decryptedMsg = decrypt(KEY, ret);

                        data.clear();
                        data = parseJson(MainActivity.this, decryptedMsg);

                        dataGrid = data.toArray(new String[0]);
                        aniGrid.setAdapter(new CustomGridAdapter(MainActivity.this, titleGrid, dataGrid));
                    } else if (res == 500) {
                        // Internal Server error
                        String err = response.body().getErrors().getMsg();
                        Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Not My API!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.wtf(TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<HealthData> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Decrypt the data from the API
     * @param seed private key to be used for the decryption
     * @param encrypted the data in String form to be encrypted
     * @return decrypted plaintext from of the data from the API
     * @throws Exception
     */
    private static String decrypt(String seed, String encrypted) throws Exception {
        byte[] keyb = seed.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
        Cipher dcipher = Cipher.getInstance("AES");
        dcipher.init(Cipher.DECRYPT_MODE, skey);

        byte[] clearbyte = dcipher.doFinal(toByte(encrypted));
        return new String(clearbyte);
    }

    /**
     * function to convert a hexadecimal string to a byte array
     * @param hexString the string to be converted
     * @return byte array from hexString to be used for decryption process
     */
    private static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        }
        return result;
    }

    /**
     * parse the decrypted plain-text response from the API
     * @param ctx context
     * @param jObj the plaintext response from API
     * @return a list of data in string form
     * @throws JSONException
     */
    private static List<String> parseJson(Context ctx, String jObj) throws JSONException {
        JSONObject jsonObject = new JSONObject(jObj);
        List<String> ret = new ArrayList<>();

        String a,b,c,d;
        a = jsonObject.getString("glu");
        b = jsonObject.getString("temp");
        c = jsonObject.getString("hrt");
        d = jsonObject.getString("bp");

        ret.add(a); ret.add(b); ret.add(c); ret.add(d);

        SharedPreferences sharedPref = ctx.getSharedPreferences("netInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("glu", a); editor.putString("temp", b); editor.putString("hrt", c); editor.putString("bp",d);
        editor.apply();

        return ret;
    }

    /**
     * Function to show diagnosis on the press of a button
     */
    private void showDiag() {
        StringBuffer buff = new StringBuffer();

        int smiley = 0x1F603;
        int goodJob = 0x1F60A;
        int ribbit = 0x1F61C;

        buff.append("You have:\n\n");
        double val;
        for (int i = 0; i < dataGrid.length; ++i) {
            val = Double.parseDouble(dataGrid[i]);
            switch (i) {
                case 0:
                    if (val>Config.HIGH_G)
                        buff.append("High Sugar - Raw cooked , roasted vegetables and low calorie drink\n\n");
                    else if (val<Config.LOW_G)
                        buff.append("Low Sugar - Don't skip meals, include snacks too , eat protein rich foods\n\n");
                    break;
                case 1:
                    if(val>Config.HIGH_T)
                        buff.append("High Temperature - Avoid spicy foods, use coconut oil for cooking\n OR - You have a fever "+getEmojiByUnicode(ribbit)+"\n\n");
                    else if (val<Config.LOW_T)
                        buff.append("Low Temperature - Eat soups, drink ice water, peanuts\n Or you have hypothermia "+getEmojiByUnicode(ribbit)+"\n\n");
                    break;
                case 2:
                    if(val>Config.HIGH_H)
                        buff.append("High Heart Rate - Eat food rich in magnesium, calcium and include fibres\n\n");
                    else if (val<Config.LOW_H)
                        buff.append("Low Heart Rate - Include fruits, vegetables, grains etc.\n\n");
                    break;
                case 3:
                    if(val>Config.HIGH_B)
                        buff.append("High BP - Reduce salt to your diet. Drink more fluids\n\n");
                    else if (val<Config.LOW_B)
                        buff.append("Low BP - Drink raw beetroot juice, strong black coffee everyday\n\n");
                    break;
            }
        }
        String msg = buff.toString();
        if (msg.equals("You have:\n\n")) {
            buff.delete(0, buff.length());
            buff.append("You are completely all right. Keep up the good work "+getEmojiByUnicode(goodJob)+"\n\n");
        }

        AlertDialog.Builder abu= new AlertDialog.Builder(MainActivity.this);
        abu.setMessage(buff.toString()).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = abu.create();
        alert.setTitle("DIAGNOSIS - Diet To Follow "+getEmojiByUnicode(smiley));
        alert.show();
    }

    /**
     * Onlick handler for gridview item
     * @param pos the position of the gridview item clicked
     */
    private void showGridDiag(int pos, String v) {
        StringBuffer buff = new StringBuffer();
        String title = "";

        int goodJob = 0x1F60A;
        int degree = 0x00B0;

        buff.append("You have:\n\n");
        double val = Double.parseDouble(v);

        switch (pos) {
            case 0:
                title = "Sugar Level";
                if (val>Config.HIGH_G)
                    buff.append("High Sugar\nNormal value is between "+Double.toString(Config.LOW_G)+" and "+Double.toString(Config.HIGH_G)+" mg/dl\n");
                else if (val<Config.LOW_G)
                    buff.append("Low Sugar\nNormal value is between "+Double.toString(Config.LOW_G)+" and "+Double.toString(Config.HIGH_G)+" mg/dl\n");
                else
                    buff.append("Normal Sugar Levels. Keep up the good work "+getEmojiByUnicode(goodJob));
                break;
            case 1:
                title = "Body Temperature";
                if (val>Config.HIGH_T)
                    buff.append("High Body Temperature\nNormal value is between "+Double.toString(Config.LOW_T)+" and "+Double.toString(Config.HIGH_T)+" "+getEmojiByUnicode(degree)+"C\n");
                else if (val<Config.LOW_T)
                    buff.append("Low Body Temperature\nNormal value is between "+Double.toString(Config.LOW_T)+" and "+Double.toString(Config.HIGH_T)+" "+getEmojiByUnicode(degree)+"C\n");
                else
                    buff.append("Normal Temperature Levels. Keep up the good work "+getEmojiByUnicode(goodJob));
                break;
            case 2:
                title = "Heart Rate";
                if (val>Config.HIGH_H)
                    buff.append("High Heart Rate\nNormal value is between "+Double.toString(Config.LOW_H)+" and "+Double.toString(Config.HIGH_H)+" bpm\n");
                else if (val<Config.LOW_H)
                    buff.append("Low Heart Rate\nNormal value is between "+Double.toString(Config.LOW_H)+" and "+Double.toString(Config.HIGH_H)+" bpm\n");
                else
                    buff.append("Normal Heart Rate. Keep up the good work "+getEmojiByUnicode(goodJob));
                break;
            case 3:
                title = "Blood Pressure";
                if (val>Config.HIGH_B)
                    buff.append("High Blood Pressure\nNormal value is between "+Double.toString(Config.LOW_B)+" and "+Double.toString(Config.HIGH_B)+"\n");
                else if (val<Config.LOW_B)
                    buff.append("Low Blood Pressure\nNormal value is between "+Double.toString(Config.LOW_B)+" and "+Double.toString(Config.HIGH_B)+"\n");
                else
                    buff.append("Normal Blood Pressure. Keep up the good work "+getEmojiByUnicode(goodJob));
                break;
        }

        AlertDialog.Builder abu= new AlertDialog.Builder(MainActivity.this);
        abu.setMessage(buff.toString()).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = abu.create();
        alert.setTitle(title);
        alert.show();
    }

    /**
     * Convert unicode to be displayed as an emoji
     * @param unicode to be converted to text
     * @return text version of emoji
     */
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
