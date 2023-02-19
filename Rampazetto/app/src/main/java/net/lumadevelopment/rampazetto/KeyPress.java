package net.lumadevelopment.rampazetto;

import android.view.KeyEvent;

public class KeyPress implements Comparable<KeyPress> {

    private Integer keyCode;
    private Long keyDown;
    private Long keyUp;

    public KeyPress(Integer keyCode, Long keyDown) {
        this.keyCode = keyCode;
        this.keyDown = keyDown;
    }

    public Integer getKeyCode() {
        return keyCode;
    }

    public Long getKeyUp() {
        return this.keyUp;
    }

    public Long getKeyDown() {
        return this.keyDown;
    }

    public void setKeyUp(Long keyUp) {
        this.keyUp = keyUp;
    }

    public boolean hasKeyUp() {
        return keyUp != null;
    }

    public void adjustToClipStart(Long audioClipStartTime) {
        keyDown -= audioClipStartTime;
        keyUp -= audioClipStartTime;
    }

    @Override
    public String toString() {

        String output = KeyEvent.keyCodeToString(keyCode) + ", Down @ " + getKeyDown();

        if(hasKeyUp()) {
            output += ", Up @ " + getKeyUp() + ", Duration: " + (getKeyUp() - getKeyDown());
        }

        return output;

    }

    @Override
    public int compareTo(KeyPress o) {

        return this.getKeyDown().compareTo(o.getKeyDown());

    }

}
