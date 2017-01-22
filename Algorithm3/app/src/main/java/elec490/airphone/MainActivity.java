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

    private static final String TAG = "MyActivity";
    short[] recordings;

    boolean mShouldContinue; // Indicates if recording / playback should stop
    int SAMPLE_RATE = 8000;
    public int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            //if buffersize < 256*2
                //error

    void recordAudio(final short[] audioInput) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean mShouldContinue = true; // Indicates if recording / playback should stop
                int SAMPLE_RATE = 8000;
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

                    if(shortsRead >= 256)
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

    public void onRadioButtonClicked(View view) {

        double lowThresh = 1.2;
        double mediumThresh = 1.3;
        double highThresh = 1.5;
        int hi;

        String output;
        double[] data = new double[256];
        for (int i = 0; i < 256; i++) {
            data[i] = 0;
        }

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_low:
                if (checked) {
                    recordAudio(recordings);
                    int dec = algorithm(recordings, lowThresh);
                    if (dec == 1)
                        playAudio(recordings);
                }
                break;

            case R.id.radio_medium:
                if (checked) {
                    //run algorithm for mid range threshold
                    if (checked) {
                        recordAudio(recordings);
                        int dec = algorithm(recordings, lowThresh);
                        if (dec == 1)
                            playAudio(recordings);
                    }
                }
                break;
            case R.id.radio_high:
                if (checked) {
                    //run algorithm with high threshold
                    if (checked) {
                        recordAudio(recordings);
                        int dec = algorithm(recordings, lowThresh);
                        if (dec == 1)
                            playAudio(recordings);
                    }
                }
                break;
        }
    }

    private static int fft_size = 256;
    private static int data_size = 256;
    private double crunch = 0.0;
    private int thresh = 119;
    private static double pi = Math.PI;
    private double[] nrg_array = new double[fft_size];
    private double[] nrg_avg_array = new double[fft_size];

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
            for (int j = 0; j < data_size; j++) {
                re = re + input[j] * Math.cos(2.0 * pi * (i * 1.0) * (j * 1.0) / data_size);
                imag = imag + ((-1.0) * input[j] * Math.sin(2.0 * pi * (i * 1.0) * (j * 1.0) / data_size));
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
        if (ctr > thresh)
            return 1;
        else
            return 0;
    }
}
