from keras.models import load_model  # TensorFlow is required for Keras to work
import cv2  # Install opencv-python
import numpy as np
import base64
import time
# Disable scientific notation for clarity
np.set_printoptions(suppress=True)

# Load the model
model = load_model("keras_Model.h5", compile=False)

# Load the labels
class_names = open("labels.txt", "r").readlines()

# CAMERA can be 0 or 1 based on default camera of your computer
camera = cv2.VideoCapture(0)

def get_hinhanh():
    retval, image = camera.read()
    retval, buffer = cv2.imencode('.jpg', image)
    image_str = base64.b64encode(buffer)
    if len(image_str) > 102400:
        # Nếu kích thước của ảnh lớn hơn 100 KB, thực hiện chia nhỏ ảnh và mã hóa từng phần
        parts = []
        for i in range(0, len(image_str), 1024):
            parts.append(image_str[i:i + 1024])
        image_str = b"".join(parts)
    return image_str

    #print(jpg_as_text)
    return jpg_as_text
#while True:
def nhandien():
    # Grab the webcamera's image.
    ret, image = camera.read()

    # Resize the raw image into (224-height,224-width) pixels
    image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)

    # Show the image in a window
    #cv2.imshow("Webcam Image", image)

    # Make the image a numpy array and reshape it to the models input shape.
    image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)

    # Normalize the image array
    image = (image / 127.5) - 1

    # Predicts the model
    prediction = model.predict(image)
    index = np.argmax(prediction)
    class_name = class_names[index]
    confidence_score = prediction[0][index]

    # Print prediction and confidence score
    #print("Class:", class_name[2:], end="")
    #print("Confidence Score:", str(np.round(confidence_score * 100))[:-2], "%")
    #print("Index:", index)



    #return class_name, confidence_score
    if index==0:
        return 1
    else:
        return 0
    # Listen to the keyboard for presses.
    #keyboard_input = cv2.waitKey(1)
    #time.sleep(1)
    # 27 is the ASCII for the esc key on your keyboard.
    #if keyboard_input == 27:
    #    break

#camera.release()
#cv2.destroyAllWindows()
