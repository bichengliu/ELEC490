package elec490.airphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayHelpMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_help_message);

        Intent intent = getIntent();
        TextView textView = new TextView(this);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_help_message);
        layout.addView(textView);
    }
}
