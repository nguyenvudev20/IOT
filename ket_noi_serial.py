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
        if "USB Serial Device" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    #return commPort
    return "COM11"

ser = serial.Serial(port=getPort(), baudrate=115200)

mess=""
def processData(client,data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split("@")
    for abc in splitData:
        aa = abc.split(":")
        print("read: "+aa[0]+"-"+aa[1])
        client.publish(aa[0], aa[1])

mess=""
def readSerial1():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            #processData(mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]
    #print(mess)

def readSerial(client):
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(client,mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]
    #print(mess)

def writeData(data):
    print("write: "+ser.name+ " data: " + data)
    ser.write(str(data).encode())

