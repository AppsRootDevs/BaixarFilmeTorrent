package com.appsrootdevs.bft;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);
        Thread splash=new Thread() {
            public void run() {

                try{

// set sleep time
                    sleep(5*1000);
                    Intent i =new Intent (getBaseContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }catch (Exception e){

                }
            }

        };
        splash.start();
    }
}
