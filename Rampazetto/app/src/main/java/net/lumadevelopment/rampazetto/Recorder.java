package net.lumadevelopment.rampazetto;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Manages audio recording for Rampazetto, a minor but
 * necessary function of the app.
 */
public class Recorder {

    // Logging
    public static final String LOG_TAG = Recorder.class.getSimpleName();

    // Instance variables
    private MediaRecorder recorder;
    private KeyPressMgr keyPressMgr;
    private boolean recording;
    private File lastRecordingObject;
    private long lastStartTime;

    // File saving variables
    public static final String RECORDING_PREFIX = "rampazettoRecording";
    public static final String RECORDING_SUFFIX = ".3gp";
    public static final String SAVE_DIRECTORY = "Rampazetto";
    private String currentFilename = "";

    public Recorder() {

        // We don't initialize the app recording
        recording = false;

        keyPressMgr = new KeyPressMgr();

    }

    // Getter method
    public boolean isRecording() {
        return recording;
    }

    public KeyPressMgr getKeyPressMgr() {
        return keyPressMgr;
    }

    /**
     * Starts recording audio and sets the recording
     * flag to true.
     */
    public void startRecording() {

        recorder = new MediaRecorder();
        lastRecordingObject = getNextRecordingFileObj();

        // Configure recorder
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(lastRecordingObject);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(44100);

        try {

            recorder.prepare();

        } catch (IOException e) {

            Log.e(LOG_TAG, "Recorder prepare() function failed!");
            Log.e(LOG_TAG, ExceptionUtils.getMessage(e));

        }

        Log.d(LOG_TAG, "Recorder successfully initialized and prepared!");

        keyPressMgr.resetKeyPresses();

        lastStartTime = System.currentTimeMillis();
        keyPressMgr.setListening(true);

        recorder.start();

        Log.d(LOG_TAG, "Recording started!");

        // UI value
        recording = true;

    }

    /**
     * Shuts down the recorder and saves the recording.
     * Turns the recording flag off.
     * @return The filename of the saved recording.
     */
    public String stopRecording() {

        keyPressMgr.setListening(false);

        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        Log.d(LOG_TAG, "Recording stopped, file saved! Submitting to audio categorizer.");

        submitToAudioCategorizer();

        recording = false;

        return currentFilename;

    }

    /**
     * Initializes the file save directory, determines the recording
     * file name, makes a file object from it, and returns it.
     * @return The File object for the next recording.
     */
    public File getNextRecordingFileObj() {

        // Initialize directory
        File saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + SAVE_DIRECTORY + File.separator);
        saveDirectory.mkdir();

        Log.d(LOG_TAG, "Save directory: " + saveDirectory.getAbsolutePath());

        // List all the files within it
        File[] files = saveDirectory.listFiles();

        // Determine how to number the file
        long highestRecordingNumber = 0;

        // Safety first!
        if (files != null) {

            for (File file : files) {

                String name = file.getName();

                if(name.startsWith(RECORDING_PREFIX) && name.endsWith(RECORDING_SUFFIX)) {

                    // If this file is one of our recordings, get the number

                    long recordingNumber = Long.parseLong(name.substring(RECORDING_PREFIX.length(), name.length() - RECORDING_SUFFIX.length()));

                    // If it's the highest number we've seen, save it
                    if(recordingNumber > highestRecordingNumber) {
                        highestRecordingNumber = recordingNumber;
                    }

                }

            }

        }

        // Increment the highest number by 1
        highestRecordingNumber += 1;

        // Craft the filename from the pre-set prefix and suffix.
        String fileName = RECORDING_PREFIX + highestRecordingNumber + RECORDING_SUFFIX;

        // Save the filename so we can toast it later
        currentFilename = fileName;

        // Make the object
        File saveFile = new File(saveDirectory, fileName);

        Log.d(LOG_TAG, "File path: " + saveFile.getAbsolutePath());

        return saveFile;

    }

    /**
     * Called by Rampazetto's onStop(), basically an
     * emergency stop for the recorder.
     */
    public void emergencyKill() {

        Log.i(LOG_TAG, "Emergency kill called, stopping suddenly!");

        recorder.release();
        recorder = null;

    }

    public void submitToAudioCategorizer() {

        List<KeyPress> keyPresses = keyPressMgr.getKeyPresses();

        Log.d(LOG_TAG, "Adjusting key presses to clip start time: " + lastStartTime);

        for (KeyPress keyPress : keyPresses) {
            keyPress.adjustToClipStart(lastStartTime);
        }

        Log.d(LOG_TAG, "Sorting key presses by key down time");

        Collections.sort(keyPresses);

        Log.d(LOG_TAG, "Submitting to audio categorizer in new thread!");
        new Thread(() -> {
            new AudioCategorizer(keyPresses, lastRecordingObject).run();
        }).start();

    }

}
