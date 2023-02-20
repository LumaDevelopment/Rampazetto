package net.lumadevelopment.rampazetto;

import android.view.KeyEvent;

/**
 * Class to represent a key press. Contains information about
 * when the key was pressed, when the key was released, and
 * what key was pressed.
 */
public class KeyPress implements Comparable<KeyPress> {

    private Integer keyCode; // from KeyEvent class
    private Long keyDown;
    private Long keyUp;

    public KeyPress(Integer keyCode, Long keyDown) {

        // Only keyCode and keyDown because when the object
        // is instantiated, the key has not been released yet.
        this.keyCode = keyCode;
        this.keyDown = keyDown;

    }

    // Used for key code directories.
    public String getKeyName() {
        return KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "");
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

    /**
     * @return True if the key has been released, false otherwise.
     */
    public boolean hasKeyUp() {
        return keyUp != null;
    }

    /**
     * Subtracts the given time from the key down and key up times.
     * Useful when clipping key presses out of files.
     * @param audioClipStartTime The start time of the audio clip to adjust to.
     */
    public void adjustToClipStart(Long audioClipStartTime) {
        keyDown -= audioClipStartTime;
        keyUp -= audioClipStartTime;
    }

    /**
     * Express the key press as a string, including key name,
     * press and release times, and key down duration.
     * @return A string representation of the key press.
     */
    @Override
    public String toString() {

        String output = getKeyName() + ", Down @ " + getKeyDown();

        if(hasKeyUp()) {
            output += ", Up @ " + getKeyUp() + ", Duration: " + (getKeyUp() - getKeyDown());
        }

        return output;

    }

    /**
     * Sort key presses by key down time.
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer if this object
     * is less than, equal to, or greater than the specified object respectively.
     */
    @Override
    public int compareTo(KeyPress o) {

        return this.getKeyDown().compareTo(o.getKeyDown());

    }

}
