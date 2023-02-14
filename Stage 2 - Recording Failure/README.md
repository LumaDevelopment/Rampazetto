# Stage 2: Recording Failure
The original idea was to record typing audio on the phone, type into a text file, grab peaks, and 
identify the keypress audio from the peaks. That way, with the combination of the text file and the 
keypress audio, the data could automatically be split from the file, labeled, and stored. However, 
despite careful tuning of the `height` and `distance` parameters on `scipy.signal.find_peaks`, I 
could not find variables that would reliably identify key presses across all portions of data. One 
set of parameters could correctly identify all key presses on the first thirty seconds, but would be 
100-300 key presses off when expanded to the whole clip. The other might get the correct number for 
the whole clip, but when the audio was narrowed down to a smaller clip, the count would be egregiously 
incorrect.

Perhaps with smarter algorithms I could make this work, but then 
<a href="https://github.com/SJWyatt">Steven Wyatt</a> suggested a smarter option: even though in the 
final product the keyboard won't be plugged into the phone, what if you plugged a keyboard into the 
phone to collect data? While recording audio, have the Rampazetto app identify keystrokes, select 
the audio based on the average length of a keystroke (~0.2 seconds), then save and sort it. I was 
trying to avoid using Java for everything, but I can't reject the brilliance of the solution.