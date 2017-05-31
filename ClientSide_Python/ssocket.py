import socket
import time
import pigpio
import threading
import atexit
import select

#connect to pigpiod daemon
pi = pigpio.pi()
 
# setup pin as an output
#pi.set_mode(LED_PIN, pigpio.OUTPUT)


for i in range(2, 27):
	pi.set_mode(i, pigpio.OUTPUT)

serversocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serversocket.bind(('', 5000))
serversocket.setblocking(0)

customsocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
customsocket.bind(('', 6001))
customsocket.setblocking(0)

responsesocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
responsesocket.bind(('', 5002))

current_pw = []
new_pw = []
stopped = 0
verifiedAddress = ''
password = 'THE_SENATE!'
responseContent = ''
sendPort = 5002

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
		while not stopped:
			m=0
			for i in range(2, len(new_pw)):
				if new_pw[i] != current_pw[i]:
					current_pw[i]=new_pw[i]
					print "Setting ", i, " to ", new_pw[i]
					pi.set_servo_pulsewidth(i, new_pw[i])
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
                        if len(verifiedAddress) and len(responseContent):
                                print "sending: ", responseContent
                                responsesocket.sendto(responseContent, (verifiedAddress[0], sendPort))
                                responseContent = ''
                        time.sleep(2)
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
                                        for c in data:
                                                val = 1000 + 10 * (ord(c)-48)
                                                if len(new_pw) <= i:
                                                        new_pw.append(val)
                                                        current_pw.append(0)
                                                else:
                                                        new_pw[i] = val
                                                        i += 1
                        else:
                                print "Unauthorized Address! GPIO Control not granted!"
        print "GPIO Socket Closing"
		
gpioSocketThread = GPIOSocketThread("GPIO_socket_thread")

gpioSocketThread.start()

def respond(key, value):
        global responseContent
        responseContent += "|||" + key + ":::" + value

def close():
	print "\n\n -- Shutting Down --"
	global stopped
	#stop pigpio
	stopped = 1
	for i in range(2, len(new_pw)):
		pi.set_servo_pulsewidth(i, 0)

while not stopped:
	try:
                ready = select.select([customsocket], [], [], 1)
                if ready[0]:
                        customData, customAddress = customsocket.recvfrom(1024)
                        if len(customData) > 0:
                                if customData == password:
                                        print "ACCESS GRANTED!"
                                        respond("access", "success")
                                        respond("test", "wow")
                                        verifiedAddress = customAddress
                                else:
                                        respond("access", "failure")
                                        print "ACCESS DENIED! RECEIVED PASSWORD: ", customData
	except(KeyboardInterrupt,SystemExit):
		close()
