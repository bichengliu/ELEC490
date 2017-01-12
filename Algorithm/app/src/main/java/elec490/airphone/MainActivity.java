package elec490.airphone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

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

        double lowThresh = 1.2;
        double mediumThresh = 1.3;
        double highThresh = 1.5;
        int decision = 0;

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.radio_low:
                if(checked){
                    //run algorithm with low threshold
                    //audio = audioSample();
                    //decision = algorithm(audio,lowThresh);
                    if(decision == 1) {
                        //ControlOtherMedia() - may include in future
                        //audioOutput(audio);
                    }
                }
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
    /* Adds settings menu if we want that option - Don't think we need it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native int algorithm(float data[], float alpha);


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
