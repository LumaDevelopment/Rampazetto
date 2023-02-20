package net.lumadevelopment.rampazetto;

import android.os.Environment;
import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Uses the key presses and the audio file from a recording session to create FFMPEG commands that
 * select key press audio clips and save them to the appropriate directory for the key pressed.
 */
public class AudioCategorizer {

    public static final String LOG_TAG = AudioCategorizer.class.getSimpleName();

    private List<KeyPress> keyPresses;
    private File audioFile;

    public AudioCategorizer(List<KeyPress> keyPresses, File audioFile) {
        this.keyPresses = keyPresses;
        this.audioFile = audioFile;
    }

    public void run() {

        Log.d(LOG_TAG, "Audio categorizer running!");

        long startTime = System.currentTimeMillis();

        // Create a file for each key press.
        for(KeyPress kp : keyPresses) {
            FFmpegKit.execute(createCommand(kp));
        }

        long endTime = System.currentTimeMillis();

        Log.d(LOG_TAG, "Cut and classified " + keyPresses.size() + " key presses in " + (endTime - startTime) + "ms");

    }

    /**
     * Creates an FFMPEG command that outputs an audio file for the given key press object.
     * @param kp The key press to capture.
     * @return The FFMPEG command.
     */
    public String createCommand(KeyPress kp) {

        // The directory for that type of key press
        File charDir = new File(Environment.getExternalStorageDirectory() + File.separator + Recorder.SAVE_DIRECTORY + File.separator + kp.getKeyName() + File.separator);

        // Make sure the directory exists
        charDir.mkdir();

        // List all files with the format "<number>.wav"
        File[] files = charDir.listFiles(new KeypressRecordingFilenameFilter());

        // Determine how to number the file
        long highestRecordingNumber = 0;

        // Safety first!
        if (files != null) {

            for (File file : files) {

                Long recordingNumber = Long.parseLong(file.getName().substring(0, file.getName().length() - 4));

                // If it's the highest number we've seen, save it
                if(recordingNumber > highestRecordingNumber) {
                    highestRecordingNumber = recordingNumber;
                }

            }

        }

        // Increment the highest number by 1
        highestRecordingNumber += 1;

        // Output file name
        String outputName = highestRecordingNumber + ".wav";

        // The start of the key press in the recording file (start 5ms for latency safety)
        String clipStart = (kp.getKeyDown() - 5) + "ms";

        // How long the key press lasts (add 5ms for latency safety)
        String clipTime = (kp.getKeyUp() + 5 - kp.getKeyDown()) + "ms";

        /**
         * -ss <time> - Clip starts here
         * -t <time> - Length of clip
         * -i <file path> - Input file
         * <output file>
         */
        String command = "-ss " + clipStart + " -t " + clipTime + " -i \"" + audioFile.getAbsolutePath() + "\" " + charDir.getAbsolutePath() + File.separator + outputName;

        Log.d(LOG_TAG, "Generated command: \"" + command + "\"");
        return command;

    }

    /**
     * Simple class with an accept() function that filters out all
     * file names that do not follow the "<number>.wav" format.
     */
    static class KeypressRecordingFilenameFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {

            // If file's extension is not ".wav", stop
            if (!name.endsWith(".wav")) {
                return false;
            }

            String nameWithoutExtension = name.substring(0, name.length() - 4);

            try {

                Long.parseLong(nameWithoutExtension);

                // File name without extension is a long
                return true;

            } catch (Exception e) {

                // File name without extension is not a long
                return false;

            }

        }

    }

}
