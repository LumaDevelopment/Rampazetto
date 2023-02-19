package net.lumadevelopment.rampazetto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Rampazetto extends AppCompatActivity {

    public static final String LOG_TAG = Rampazetto.class.getSimpleName();
    private Recorder recorder;

    // Request permissions
    private static final int PERMISSIONS_GRANTED_CODE = 200;
    private boolean permissionsAccepted = false;
    final private String [] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE};

    // UI Text
    public static final String RECORDING_BUTTON_TEXT = "Stop Recording";
    public static final String NOT_RECORDING_BUTTON_TEXT = "Start Recording";

    /**
     * Response to permissions request. permissionsAccepted variable could be
     * used in the future if necessary, but this is just a research
     * application/personal project.
     *
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_GRANTED_CODE) {
            permissionsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        if (!permissionsAccepted) finish();

    }

    /**
     * Launches when the app starts.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Display UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permissions
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_GRANTED_CODE);

        // Brings up the screen to give the app the permission to manage all files
        // if it doesn't have it already.
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivity(intent);
        }

        // Initialize Recorder object
        recorder = new Recorder();

        EditText editText = findViewById(R.id.textField);

        editText.setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                recorder.getKeyPressMgr().onKeyDown(keyCode, event);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                recorder.getKeyPressMgr().onKeyUp(keyCode, event);
            }

            return true;

        });

        // Create recording button.
        Button recordingToggleButton = findViewById(R.id.recordingToggle);

        recordingToggleButton.setOnClickListener(view -> {

            if(recorder.isRecording()) {

                // If recorder is recording, then stop recording, save the file,
                // and toast the file name.
                toast("Recording saved to " + recorder.stopRecording());
                recordingToggleButton.setText(NOT_RECORDING_BUTTON_TEXT);

            } else {

                // Start recording
                recorder.startRecording();
                recordingToggleButton.setText(RECORDING_BUTTON_TEXT);

            }

        });

    }

    /**
     * When the app shuts down.
     */
    @Override
    public void onStop() {

        super.onStop();

        // Quick stop for recorder
        if (recorder != null && recorder.isRecording()) {
            recorder.emergencyKill();
        }

    }

    /**
     * Utility function
     * Just shortens the process of toasting by reducing it
     * to a function call and a message rather than Toast.makeText().show()
     * @param message The message to toast.
     */
    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}