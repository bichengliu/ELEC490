package com.example.bicheng.myapplication;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private int low = 0;
    private int med = 0;
    private int high = 0;
    public int btn= 0;
    TestThread Points = new TestThread();
    TextView temp;
    private boolean keepRunning = true;
    public void onClicked(View view){
        final ToggleButton but1 = (ToggleButton)findViewById(R.id.radio_low);
        final ToggleButton but2 = (ToggleButton)findViewById(R.id.radio_medium);
        final ToggleButton but3 = (ToggleButton)findViewById(R.id.radio_high);


        but1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                but2.setChecked(false);
                but3.setChecked(false);



                temp = (TextView)findViewById(R.id.Text1);
                btn = 1;

                if(!Points.isAlive()) {
                    keepRunning = true;
                    Points.start();
                }
                else Points.cancel();
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                but3.setChecked(false);
                but1.setChecked(false);

                btn = 2;
                temp = (TextView)findViewById(R.id.Text2);
                if(!Points.isAlive()){
                    keepRunning = true;
                    Points.start();
                }else Points.cancel();
            }
        });

        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                but2.setChecked(false);
                but1.setChecked(false);



                btn = 3;
                temp = (TextView)findViewById(R.id.Text3);
                if(!Points.isAlive()){
                    keepRunning = true;
                    Points.start();
                }else Points.cancel();
            }
        });

    }

    public class TestThread extends Thread
    {
        private String points;
       // private boolean keepRunning = true;

        private int low;
        private int med;
        private int high;

        public void cancel(){
            keepRunning = false;
        }

        @Override
        public void run()
        {
            while (true)
            {
                if(!keepRunning) break;

                try
                {
                    if(btn == 1){
                        low++;
                    }
                    if(btn == 2){
                        med++;
                    }
                    if(btn == 3){
                        high++;
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(btn == 1){
                                temp.setText(Integer.toString(low));
                            }
                            if(btn == 2){
                                temp.setText(Integer.toString(med));
                            }
                            if(btn == 3){
                                temp.setText(Integer.toString(high));
                            }

                        }
                    });

                    Thread.sleep(500);

                } catch (InterruptedException e) {}
            }
        }
    }

}