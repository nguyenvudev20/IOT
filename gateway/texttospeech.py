from gtts import gTTS
import os
from mpyg321.MPyg123Player import MPyg123Player
# The text that you want to convert to audio
#mytext = 'Chiều nay không có mưa bay ?'
# player = MPyg123Player()
# player.play_song("F:/hoc/mse.fpt.2022/IOT/welcome.mp3", loop=True)
os.system("welcome.mp3")
def textttalk(mytext):
    # Language in which you want to convert
    language = 'vi'

    # Passing the text and language to the engine,
    # here we have marked slow=False. Which tells
    # the module that the converted audio should
    # have a high speed
    myobj = gTTS(text=mytext, lang=language, slow=False)

    # Saving the converted audio in a mp3 file named
    # welcome
    myobj.save("welcome.mp3")
    #player = MPyg123Player()
    #player.play_song("welcome.mp3")

#textttalk("Chiều nay không có mưa bay ?")