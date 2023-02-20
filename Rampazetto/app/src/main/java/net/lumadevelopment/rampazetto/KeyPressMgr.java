package net.lumadevelopment.rampazetto;

import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages key presses for recordings. Keeps track of when and what
 * keys are pressed and released.
 */
public class KeyPressMgr {

    public static final String LOG_TAG = KeyPressMgr.class.getSimpleName();

    /**
     * List of key presses for current recording.
     */
    private List<KeyPress> keyPresses;

    /**
     * If a key is being held down, then onKeyDown()
     * will get triggered multiple times. We don't want
     * duplicate KeyPress objects with the same key
     * code, so we keep track of if a key has been pressed
     * and not released, and if so, we create no new
     * KeyPress objects for that key.
     */
    private HashMap<Integer, Boolean> isKeyPressed;

    /**
     * True if we're currently recording audio and trying to get key presses.
     */
    private boolean isListening;

    public KeyPressMgr() {

        keyPresses = new ArrayList<>();
        isKeyPressed = new HashMap<>();
        isListening = false;

    }

    public boolean isListening() {
        return isListening;
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    /**
     * Called when a key is pressed in the Recording text box. If we're not
     * recording audio, then do nothing. If we are, check if the key is just
     * being held down. If not, then add to the list of key presses.
     * @param keyCode The key code of the key that was pressed.
     */
    public void onKeyDown(int keyCode) {

        if(!isListening()) {
            // Not recording
            return;
        }

        // Duplicate prevention, Boolean.TRUE for null handling
        if(Boolean.TRUE.equals(isKeyPressed.get(keyCode))) {
            return;
        }

        // Add new key press object and set key as pressed
        KeyPress keyPress = new KeyPress(keyCode, System.currentTimeMillis());
        keyPresses.add(keyPress);
        isKeyPressed.put(keyCode, true);

        Log.d(LOG_TAG, "Previously unpressed key \"" + keyPress.getKeyName() + "\" pressed!");

    }

    public void onKeyUp(int keyCode) {

        if(!isListening()) {
            // Not recording
            return;
        }

        // Check for all KeyPress objects with this code
        for(KeyPress kp : pressesForKeyCode(keyCode)) {

            if(!kp.hasKeyUp()) {

                // Key has not been released yet, so set the key up time
                kp.setKeyUp(System.currentTimeMillis());

                // Set key as not pressed
                isKeyPressed.put(keyCode, false);

                Log.d(LOG_TAG, "Previously pressed key \"" + kp.getKeyName() + "\" released!");

                return;

            }

        }

    }

    /**
     * Retrieve all KeyPress objects with the given key code.
     * @param keyCode The key code to search for.
     * @return A list of KeyPress objects with the given key code.
     */
    private List<KeyPress> pressesForKeyCode(int keyCode) {

        List<KeyPress> presses = new ArrayList<>();

        for (KeyPress press : keyPresses) {

            if (press.getKeyCode() == keyCode) {
                presses.add(press);
            }

        }

        return presses;

    }

    public List<KeyPress> getKeyPresses() {
        return keyPresses;
    }

    // Used for new recordings.
    public void resetKeyPresses() {
        keyPresses = new ArrayList<>();
    }

}
