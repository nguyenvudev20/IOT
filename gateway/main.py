import sys
from Adafruit_IO import MQTTClient,Client, Feed
import time
from ket_noi_serial import readSerial
from ket_noi_serial import writeData
import random
from simple_ai import nhandien
from simple_ai import get_hinhanh


#AIO_FEED_ID_BTN = ["button1","button2","dimmer"]
#AIO_FEED_ID_CB = ["sensor1","sensor3"]

AIO_FEED_ID_BTN = ["btn1","btn2","dimmer"]
AIO_FEED_ID_CB = ["cb1","cb2"]

Time_Feed=[5,10]
<<<<<<< HEAD
AIO_USERNAME = ""
AIO_KEY = ""
#AIO_USERNAME = ""
#AIO_KEY = ""
=======
AIO_USERNAME = ""
AIO_KEY = ""
>>>>>>> dc57693e945990423ab7d5161cdd6b75706c2de5
btnNhietDo=0
btnDoAm=0

def connected(client):
    print("Ket noi thanh cong ...")
    for feed in AIO_FEED_ID_BTN:
        client.subscribe(feed)

def subscribe(client , userdata , mid , granted_qos):
    print("Subscribe thanh cong ...")

def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit(1)

def message(client , feed_id , payload):
    #print("Nhan du lieu : " +feed_id +" val="+payload)
    if feed_id == 'btn1':
        if payload=="0":
            writeData("1")
        elif payload=="1":
            writeData("2")
    elif feed_id == 'btn2':
        if payload == "0":
            writeData("3")
        elif payload == "1":
            writeData("4")
    elif feed_id == 'dimmer':
        writeData("DM:"+str(payload))

    # if feed_id == 'button1':
    #     if payload == "0":
    #         writeData("1")
    #     elif payload == "1":
    #         writeData("2")
    # elif feed_id == 'button2':
    #     if payload == "0":
    #         writeData("3")
    #     elif payload == "1":
    #         writeData("4")
    # elif feed_id == 'dimmer':
    #     writeData("DM:" + str(payload))

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()




dem=0
cambien=0
dem_nhan_dien=0
time_renew=60
while True:

    #print(str(btnDoAm) + '  ' + str(btnNhietDo))
    #if dem==Time_Feed[cambien]:
    #    rd=random.randint(10,100)
    #    print("Gui : "+AIO_FEED_ID_CB[cambien] +" val="+str(rd))
    #    client.publish(AIO_FEED_ID_CB[cambien], rd)
    #    cambien=cambien+1
    #    if cambien==len(AIO_FEED_ID_CB):
    #        cambien=0
    #
    #if dem>=max(Time_Feed):
    #    dem=0
    #dem=dem+1
    # if dem ==5:
    if time_renew >=60:
        kq =nhandien()
        if(kq==1):
            dem_nhan_dien = dem_nhan_dien + 1
        else:
            dem_nhan_dien=0
    elif time_renew<=0:
        time_renew=60
    else:
        time_renew=time_renew-1

    if dem_nhan_dien>=10:
        writeData("NN:1")
        dem_nhan_dien=0
        hinh=get_hinhanh()
        client.publish('hinhanh', hinh)
        #print(hinh)
        time_renew=59

    print("đếm không khẩu trang: ",dem_nhan_dien," time_renew: ",time_renew)
    readSerial(client)
    time.sleep(1)
