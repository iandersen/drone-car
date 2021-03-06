import socket
import time
import pigpio
import threading
import atexit
import select
import hashlib
import os
from gps3 import gps3
import re

#time.sleep(10)
#connect to pigpiod daemon
pi = pigpio.pi()

# setup pin as an output
#pi.set_mode(LED_PIN, pigpio.OUTPUT)


for i in range(2, 27):
    pi.set_mode(i, pigpio.OUTPUT)
    pi.write(i, 1)

serversocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serversocket.bind(('', 5000))
serversocket.setblocking(0)

customsocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
customsocket.bind(('', 5001))
customsocket.setblocking(0)

current_pw = []
new_pw = []
stopped = 0
verifiedAddress = ''
sha = hashlib.new('sha256')
sha.update('THE_SENATE!')
password = sha.hexdigest()
responseContent = ''
sendPort = 0
streamStarted = 0

#GPS Things
lat = 0;
totalLat = 0;
totalLon = 0;
ticksIdle = 0;
lon = 0;
alt = 0;
gpsTime = '0';
gpsSpeed = 0;
climb = 0;
hasConnectedToGPS = 0;
averagePosWhileStill = 1;
hasMoved = 0;

class GpsPoller(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.socket = gps3.GPSDSocket()
        self.stream = gps3.DataStream()
        self.socket.connect()
        self.socket.watch()
        self.running = True
        print "Initializing GPS"
    def run(self):
        global lat
        global lon
        global alt
        global gpsTime
        global climb
        global gpsSpeed
        global hasConnectedToGPS
        global stopped
        global hasMoved
        global totalLat
        global totalLon
        global ticksIdle
        while not stopped:
            for new_data in self.socket:
                if new_data:
                    self.stream.unpack(new_data)
                    if self.stream.TPV['lat'] != "n/a" and hasMoved:
                        lat = self.stream.TPV['lat']
                        lon = self.stream.TPV['lon']
                        totalLat = 0;
                        totalLon = 0;
                        ticksIdle = 0;
                    elif self.stream.TPV['lat'] != "n/a":
                        totalLat += float(self.stream.TPV['lat'])
                        totalLon += float(self.stream.TPV['lon'])
                        ticksIdle+=1
                        lat = totalLat/ticksIdle
                        lon = totalLon/ticksIdle
                    alt = self.stream.TPV['alt']
                    gpsTime = self.stream.TPV['time']
                    gpsSpeed = self.stream.TPV['speed']
                    climb = self.stream.TPV['climb']
                    if lat != 'n/a':
                        respond('position',str(lat)+','+str(lon))
                        respond('speed',str(gpsSpeed))
                        respond('alt',str(alt))
                    if hasConnectedToGPS == 0 and lat != 'n/a':
                        hasConnectedToGPS = 1
                        respond("debug", 'GPS Connection Established!')

gpsp = GpsPoller()
gpsp.start()

def respond(key, value):
        global responseContent
        responseContent += "|||" + key + ":::" + value

class GPIOThread(threading.Thread):
    def __init__(self, name):
        threading.Thread.__init__(self)
        self.name = name
        print "GPIO Control Thread Initialized!"

    def run(self):
        global pw
        global pulsewidth
        global pi
        global stopped
        global hasMoved
        while not stopped:
            m=0
            for i in range(2, len(new_pw)):
                hasMoved = 0;
                if new_pw[i] != current_pw[i]:
                    hasMoved = 1;
                    current_pw[i]=new_pw[i]
                    print "Setting ", i, " to ", new_pw[i]
                    if new_pw[i] >= 500:
                        pi.set_servo_pulsewidth(i, new_pw[i])
                    else:
                        pi.set_servo_pulsewidth(i, 0)
                        pi.write(i, not new_pw[i])
            time.sleep(0.05)
        print "GPIO output Terminated!"

gpioThread = GPIOThread("GPIO_thread")

gpioThread.start()

class RespondThread(threading.Thread):
    def __init__(self, name):
        threading.Thread.__init__(self)
        self.name = name
        print "Respond Thread Initialized!"

    def run(self):
        global responseContent
        global sendPort
        global verifiedAddress
        global stopped
        while not stopped:
                        respond("0", "1")#Inform the controller that the connection is alive
                        if len(verifiedAddress) and len(responseContent):
                                customsocket.sendto(responseContent, (verifiedAddress[0], sendPort))
                        responseContent = ""
                        time.sleep(1)
        print "Response Thread Closing"

respondThread = RespondThread("RespondThread")

respondThread.start()

class GPIOSocketThread(threading.Thread):
    def __init__(self, name):
        threading.Thread.__init__(self)
        self.name = name
        print "GPIO Socket Thread Initialized!"

    def run(self):
        global verifiedAddress
        global new_pw
        global current_pw
        global stopped
        while not stopped:
                time.sleep(0.05)
                ready = select.select([serversocket], [], [], 1)
                if ready[0]:
                        data, address = serversocket.recvfrom(64)
                        if address and verifiedAddress and address[0] == verifiedAddress[0]:
                                if len(data) > 0:
                                        i = 0
                                        beforeStart = -1
                                        for c in data:
                                                val = (ord(c)-1)
                                                if beforeStart != -1:
                                                        while len(new_pw) <= i + beforeStart:
                                                                new_pw.append(0)
                                                                current_pw.append(0)
                                                if i == 0:
                                                        beforeStart = val - 1
                                                elif val <= 200:
                                                        pw = 500 + 10 * val
                                                        if new_pw[beforeStart + i] < 500:
                                                                current_pw[beforeStart + i] = pw
                                                        new_pw[beforeStart + i] = pw
                                                else:
                                                        on = 0 if val == 201 else 1
                                                        new_pw[beforeStart + i] = on
                                                i += 1
                        else:
                                respond("error", "Unauthorized Address! GPIO Control not granted!")
                                print "Unauthorized Address! GPIO Control not granted!"
        print "GPIO Socket Closing"

gpioSocketThread = GPIOSocketThread("GPIO_socket_thread")

gpioSocketThread.start()

def close():
    print "\n\n -- Shutting Down --"
    global stopped
    global streamStarted
    #stop pigpio
    stopped = 1
    if streamStarted:
                os.system('./stream_stop.sh')
                print "Stopping Stream"
    for i in range(2, len(new_pw)):
        pi.set_servo_pulsewidth(i, 0)
        pi.write(i, 1)

while not stopped:
    try:
                ready = select.select([customsocket], [], [], 1)
                if ready[0]:
                        customData, customAddress = customsocket.recvfrom(1024)
                        if len(customData) > 0:
                                if customData == password:
                                        if not verifiedAddress or customAddress[0] == verifiedAddress[0]:
                                                print "ACCESS GRANTED!"
                                                sendPort = customAddress[1]
                                                respond("access", "success")
                                                verifiedAddress = customAddress
                                        else:
                                                respond("access", "failure")
                                                print "ACCESS DENIED! BAD ADDRESS"
                                elif verifiedAddress and customAddress[0] == verifiedAddress[0]:
                                        if customData == "stream_start":
                                                if not streamStarted:
                                                        print "STARTING STREAM"
                                                        os.system('./stream_start.sh')
                                                        respond("debug", "Stream initialized!")
                                                        streamStarted = 1
                                                else:
                                                        print "Stream already started"
                                                        respond("warn", "Stream already started!")
                                        elif customData == "stream_stop":
                                                if streamStarted:
                                                        print "STOPPING STREAM"
                                                        os.system('./stream_stop.sh')
                                                        respond("debug", "Stream Terminated!")
                                                        streamStarted = 0
                                                else:
                                                        print "Stream not running"
                                                        respond("warn", "Cancel failed, stream not running")
                                        elif customData == "take_screenshot":
                                                print "TAKING SCREENSHOT"
                                                os.system('./screenshot.sh')
                                                respond("debug", "Screenshot taken!")
                                        elif customData == "ping":
                                                customsocket.sendto("|||ping:::ping", (verifiedAddress[0], sendPort))
                                        elif re.match(customData, 'speech'):
                                            os.system('./speak.sh "' + customData.split('|', 2)[1] +'"')
                                            respond("debug", "Just said: " + customData.split('|', 2)[1]);
                                        else:
                                                print "UNRECOGNIZED COMMAND: ", customData
                                                respond("error", "UNRECOGNIZED COMMAND: " +str(customData))
    except(KeyboardInterrupt,SystemExit):
        close()
