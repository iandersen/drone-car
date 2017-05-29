import socket
import time
import pigpio
import threading
import atexit

#connect to pigpiod daemon
pi = pigpio.pi()

for i in range(2, 27):
    pi.set_mode(i, pigpio.OUTPUT)

serversocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serversocket.bind(('', 5000))

current_pw = []
new_pw = []
stopped = 0

class GPIOThread(threading.Thread):
     def __init__(self, name):
    threading.Thread.__init__(self)
    self.name = name
        print "Thread Initialized!"

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
        print "Thread Terminated!"
gpioThread = GPIOThread("GPIO_thread")

gpioThread.start()

def close():
    print "Stopping PIGPIO"
    global stopped
    #stop pigpio
    stopped = 1
    time.sleep(.2)
    for i in range(2, len(new_pw)):
        pi.set_servo_pulsewidth(i, 0)

while not stopped:
    try:
            data, address = serversocket.recvfrom(64)
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
    except(KeyboardInterrupt,SystemExit):
        close()
