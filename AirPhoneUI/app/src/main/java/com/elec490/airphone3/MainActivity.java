package com.elec490.airphone3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void helpButton(View view){
        Intent intent = new Intent(this, DisplayHelpMessage.class);
        startActivity(intent);
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.radio_low:
                if(checked)
                    //run algorithm with low threshold
                    break;
            case R.id.radio_medium:
                if(checked)
                    //run algorithm for mid range threshold
                    break;
            case R.id.radio_high:
                if(checked)
                    //run algorithm with high threshold
                    break;
        }
    }
}
