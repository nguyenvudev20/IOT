import serial.tools.list_ports
import random
import time
import sys


def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB-SERIAL" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort
    #return "COM12"

ser = serial.Serial(port=getPort(), baudrate=115200)

mess=""
def processData(client,data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "T":
        client.publish("cb1", splitData[2])
    elif splitData[1] == "H":
        client.publish("cb2", splitData[2])
    elif splitData[1] == "lenh":
         client.publish("khautrang",'Gui lenh: ' +splitData[2] +' thanh cong')


def processData2(client,data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "1":
        client.publish("cb1", splitData[2])
    elif splitData[1] == "H":
        client.publish("2", splitData[2])
    elif splitData[1] == "lenh":
         client.publish("khautrang",'Gui lenh: ' +splitData[2] +' thanh cong')



mess=""
def readSerial(client):
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode()
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(client,mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]
    #print("mess: "+mess)

def writeData(data):
    if ser.is_open:
        #print("write: " + ser.name + " data: " + data)
        ser.write(str(data).encode())
        # Đọc dữ liệu phản hồi từ serial port
        # Kiểm tra dữ liệu phản hồi để xác định nếu lệnh đã được gửi thành công hay không
        # dem=0
        # while True:
        #     response = ser.inWaiting()
        #     if data == response:
        #         print('Gửi lệnh: ' + data + ' thành công')
        #         break
        #     if dem==3:
        #         print('Gửi lệnh: ' + data + ' thất bại')
        #         break
        #     dem=dem+1
        #     time.sleep(1)
    else:
        print("Không thể kết nối với cổng serial.")
        ser.close()
        exit()

# while True:
#     readSerial()
#     time.sleep(5)