package net.lumadevelopment.rampazetto;

import java.io.File;
import java.util.List;

public class AudioCategorizer {

    public static final String LOG_TAG = AudioCategorizer.class.getSimpleName();

    private List<KeyPress> keyPresses;
    private File audioFile;

    public AudioCategorizer(List<KeyPress> keyPresses, File audioFile) {
        this.keyPresses = keyPresses;
        this.audioFile = audioFile;
    }

    public void run() {



    }

}
