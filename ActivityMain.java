package com.ancientlore.alspacer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityMain extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs =
                getSharedPreferences("HiScores",MODE_PRIVATE);
        SharedPreferences.Editor editor;

        final Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);

        long fastestTime = prefs.getLong("fastestTime",1000000);
        final TextView textFastestTime= (TextView)findViewById(R.id.textViewHiScore);
        textFastestTime.setText("Fastest Time:"+fastestTime);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonPlay:
                Intent i = new Intent(this,ActivityGame.class);
                startActivity(i);
                finish();
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        else return  false;
    }
}
