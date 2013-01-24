package com.example.poc_bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;



public class ResultActivity extends Activity {

  private String mResultStr = null;
  private TextView mTextview1 = null;
  
    
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);
    
    Intent intent = getIntent();
    mResultStr =  intent.getStringExtra("key");
    
    mTextview1 = (TextView) findViewById(R.id.textview1);
    mTextview1.setText(mResultStr);
    
  }
  
  
}
