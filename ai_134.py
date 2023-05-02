import cv2
from ai_134_2

video=cv2.VideoCapture(1)
a=0
i=0

while True:
    a = a + 1
    check, frame = video.read()
    #cv2.imshow("Capturing", frame)

    key=cv2.waitKey(1)

    if key == ord ('r'):
        fourcc = cv2.VideoWriter_fourcc(*'XVID')
        out = cv2.VideoWriter('output.avi',fourcc, 20.0, (640,480))
        out.write(frame)
    if key == ord ('x'):
        name = input("Nhap ten: ")
        print(name)
        i+=1
        cv2.imwrite("face/"+name+'_'+str(i)+'.jpg', frame)
        cv2.imshow("User Capture", frame)
        print('taking pictures')
    if key == ord ('q'):
        break


print(a)
video.release()
cv2.destroyAllWindows