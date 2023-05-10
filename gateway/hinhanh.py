import cv2
import base64

def get_hinhanh():
    cap = cv2.VideoCapture(0)
    retval, image = cap.read()
    retval, buffer = cv2.imencode('.jpg', image)
    #resized_image = cv2.resize(buffer, (200, 200))
    jpg_as_text = base64.b64encode(buffer)
    #print(jpg_as_text)
    cap.release()
    return jpg_as_text