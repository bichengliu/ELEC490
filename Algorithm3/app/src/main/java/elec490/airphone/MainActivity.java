package elec490.airphone;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.RadioButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.nio.ShortBuffer;

import static android.media.AudioTrack.MODE_STREAM;
import static android.media.AudioTrack.getMinBufferSize;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void helpButton(View view) {
        Intent intent = new Intent(this, DisplayHelpMessage.class);
        startActivity(intent);
    }
    private static int fft_size = 1024;
    private static int outputSizeDiff = 8;
    private static int recording_size = 7680;
    private static int outputSize = fft_size*outputSizeDiff;
    private double crunch = 0.0;
    private int thresh = 15;
    private static double pi = Math.PI;
    private double[] nrg_array = new double[fft_size];
    private double[] nrg_avg_array = new double[fft_size];
    private int btn = 0;
    private double lowThresh = 1.05;
    private double mediumThresh = 1.1;
    private double highThresh = 1.2;
    TestThread button_algo = new TestThread();
    short[] recordings = new short[fft_size];
    short[] audioOutputs = new short[outputSize];

    public void onRadioButtonClicked(View view) {


        String output;
        double[] data = new double[fft_size];
        for (int i = 0; i < fft_size; i++) {
            data[i] = 0;
        }

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_low:
                if (checked) {
                    btn = 1;
                    Log.v(TAG, "Button One");
                    if(!button_algo.isAlive()){
                        button_algo.start();
                    }
                }
                break;

            case R.id.radio_medium:
                if (checked) {
                    //run algorithm for mid range threshold
                    btn = 2;
                    Log.v(TAG, "Button Two");
                    if(!button_algo.isAlive()){
                        button_algo.start();
                    }

                }
                break;
            case R.id.radio_high:
                if (checked) {
                    //run algorithm with high threshold
                    if (checked) {
                        btn = 3;
                        Log.v(TAG, "Button Three");
                        if(!button_algo.isAlive()){
                            button_algo.start();
                        }
                    }
                }
                break;
        }
    }
    private static final String TAG = "MyActivity";


    boolean mShouldContinue; // Indicates if recording / playback should stop
    int SAMPLE_RATE = 44100;
    public int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            //if buffersize < 256*2
                //error

    void recordAudio(final short[] audioInput) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean mShouldContinue = true; // Indicates if recording / playback should stop
                int SAMPLE_RATE = 44100;
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];
                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(TAG, "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();

                Log.v(TAG, "Start recording");

                long shortsRead = 0;
                while (mShouldContinue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;

                    if(shortsRead >= fft_size)
                        mShouldContinue = false;
                }


                for(int i = 0; i < fft_size; i++)
                    audioInput[i] = audioBuffer[i];
                record.stop();
                record.release();

                Log.v(TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }


    AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
            bufferSize, MODE_STREAM);

    void playAudio(final short[] audio) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int bufferSize = getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                Log.v(TAG, String.format("Buffer size of Output: %d", bufferSize));
                if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize, MODE_STREAM);

                audioTrack.play();
                audioTrack.write(audio, 0, bufferSize);
                audioTrack.stop();
                audioTrack.release();

                }
        }).start();
    }





    public void startup(double[] populate_nrg_array, double[] populate_nrg_avg_array) {
        for (int i = 0; i < fft_size; i++) {
            populate_nrg_array[i] = 0;
            populate_nrg_avg_array[i] = 0;
        }
    }

    public int algorithm(short[] data, double alpha) {

        int decision_value;
        double fftoutput[] = new double[fft_size];
        my_fft(fftoutput, data);
        nrg_sum(nrg_array, fftoutput);

        if(crunch > 500){
            reset_nrg(nrg_array,nrg_avg_array);
        }

        crunch = crunch + 1.0;
        nrg_avg(nrg_avg_array, nrg_array, crunch);
        decision_value = decision(nrg_avg_array, fftoutput, alpha);
        return decision_value;

    }

    public void my_fft(double[] fftoutput, short[] in) {
        double re, imag, pwrRe = 0.0, pwrImag = 0.0;
        double input[] = new double[fft_size];
        for(int i= 0; i < fft_size; i++){
            input[i] = (double) in[i]*1.0;
        }
        for (int i = 0; i < fft_size; i++) {

            re = 0.0;
            imag = 0.0;
            for (int j = 0; j < fft_size; j++) {
                re = re + input[j] * Math.cos(2.0 * pi * (i * 1.0) * (j * 1.0) / fft_size);
                imag = imag + ((-1.0) * input[j] * Math.sin(2.0 * pi * (i * 1.0) * (j * 1.0) / fft_size));
                pwrRe = Math.abs(re) * Math.abs(re);
                pwrImag = Math.abs(imag) * Math.abs(imag);
            }
            fftoutput[i] = pwrRe + pwrImag;
        }
    }

    public void nrg_sum(double[] sum_array, double[] in) {
        for (int i = 0; i < fft_size; i++) {
            sum_array[i] = sum_array[i] + in[i];
        }
    }

    public void nrg_avg(double[] avg_array, double[] in, double total_crunch) {
        for (int i = 0; i < fft_size; i++) {
            avg_array[i] = in[i] / total_crunch;
        }
    }

    public int decision(double[] avg_array, double[] in, double alpha) {
        int i;
        int ctr = 0;
        for (i = 0; i < fft_size; i++) {
            if (in[i] > avg_array[i] * alpha)
                ctr++;
        }
        if (ctr > thresh) {
            Log.v(TAG, String.format("One"));
            return 1;
        }
        else {
            Log.v(TAG, String.format("Zero"));
            return 0;
        }
    }
    public void reset_nrg(double[] nrg_array, double[] nrg_avg_array){
        for(int i = 0; i < fft_size; i++) {
            nrg_array[i] = nrg_avg_array[i];
            nrg_avg_array[i] = 0;
        }
        crunch = 0;
    }
    public class TestThread extends Thread
    {
        private String points;
        private boolean keepRunning = true;
        public void cancel(){
            keepRunning = false;
        }

        @Override
        public void run()
        {
            while(true)
            {
                if(!keepRunning) break;

                try
                {
                    if(btn == 1){
                        recordAudio(recordings);
                        int dec = algorithm(recordings, lowThresh);
                        if (dec == 1) {
                            for (int i = 0; i < outputSizeDiff; i++) {
                                for(int j = 0; j < fft_size; j++){
                                    audioOutputs[fft_size*i+j] = recordings[j];
                                }
                            }
                            playAudio(audioOutputs);
                        }
                    }
                    if(btn == 2) {
                        recordAudio(recordings);
                        int dec = algorithm(recordings, mediumThresh);
                        if (dec == 1) {
                            for (int i = 0; i < outputSizeDiff; i++) {
                                for(int j = 0; j < fft_size; j++){
                                    audioOutputs[fft_size*i+j] = recordings[j];
                                }
                            }
                            playAudio(audioOutputs);
                        }
                    }
                    if(btn == 3) {
                        recordAudio(recordings);
                        int dec = algorithm(recordings, highThresh);
                        if (dec == 1) {
                            for (int i = 0; i < outputSizeDiff; i++) {
                                for(int j = 0; j < fft_size; j++){
                                    audioOutputs[fft_size*i+j] = recordings[j];
                                }
                            }
                            playAudio(audioOutputs);
                        }
                    }

                    Thread.sleep(5);

                } catch (InterruptedException e) {}
            }
        }
    }
}
