import librosa
import scipy.signal

audio, sampleRate = librosa.load('rampazettoRecording1.3gp')

print(audio.shape, sampleRate)

keypressIndices = scipy.signal.find_peaks(audio, height=0.0244, distance=8820)

print("Number of keypresses: ", len(keypressIndices[0]))
