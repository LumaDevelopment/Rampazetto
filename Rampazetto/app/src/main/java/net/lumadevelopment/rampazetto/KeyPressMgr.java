package net.lumadevelopment.rampazetto;

import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class KeyPressMgr {

    public static final String LOG_TAG = KeyPressMgr.class.getSimpleName();

    private List<KeyPress> keyPresses;
    private boolean isListening;

    public KeyPressMgr() {

        keyPresses = new ArrayList<>();
        isListening = false;

    }

    public boolean isListening() {
        return isListening;
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    public void onKeyDown(int keyCode, KeyEvent event) {

        if(!isListening()) {
            return;
        }

        for(KeyPress kp : pressesForKeyCode(keyCode)) {

            if(!kp.hasKeyUp()) {
                return;
            }

        }

        keyPresses.add(new KeyPress(keyCode, System.currentTimeMillis()));

    }

    public void onKeyUp(int keyCode, KeyEvent event) {

        if(!isListening()) {
            return;
        }

        for(KeyPress kp : pressesForKeyCode(keyCode)) {

            if(!kp.hasKeyUp()) {
                kp.setKeyUp(System.currentTimeMillis());
                return;
            }

        }

    }

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

    public void resetKeyPresses() {
        keyPresses = new ArrayList<>();
    }

}
