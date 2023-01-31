# Make an array containing all the letters of the alphabet
alphabet = []
for letter in range(97, 123):
    alphabet.append(chr(letter))


# Take a string and return a list of all the letters in the string
def get_letters(string):
    letters = []
    for loop_letter in string:
        # Make the letter lowercase
        loop_letter = loop_letter.lower()
        letters.append(loop_letter)
    return letters


print("Paste essay. Ctrl-D (Mac/Linux)/Ctrl-Z (Windows) to end.")
userInput = ""

while True:
    try:
        line = input()
    except EOFError:
        break
    userInput += line

# Count the frequency of each letter in userInput
userInputLetters = get_letters(userInput)
letterCounts = []

for letter in alphabet:
    letterCount = userInputLetters.count(letter)
    letterCounts.append(letterCount)
    print(letter + ": " + str(letterCount))

print("Average letter occurrence: " + str(sum(letterCounts) / len(letterCounts)))
