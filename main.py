import sys
from Adafruit_IO import MQTTClient,Client, Feed
import time
from ket_noi_serial import readSerial
from ket_noi_serial import writeData
import random
# from simple_ai import nhandien
from hinhanh import get_hinhanh


AIO_FEED_ID_BTN = ["btn1","btn2"]
AIO_FEED_ID_CB = ["cb1","cb2"]
Time_Feed=[5,10]
AIO_USERNAME = ""
AIO_KEY = ""
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
    print("Nhan du lieu : " +feed_id +" val="+payload)
    if feed_id == 'btn1':
        writeData(str(payload))
    elif feed_id == 'btn2':
        writeData(str(payload))

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()



dem=0
cambien=0

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
    #     #kqnhandien=nhandien()
    #     hinh=get_hinhanh()
    #     client.publish('hinhanh', hinh)
    #     #print(kqnhandien)
    #     dem=0
    # dem=dem+1
    readSerial(client)
    time.sleep(1)
