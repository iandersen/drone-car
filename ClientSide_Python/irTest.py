import pigpio
import time

pi = pigpio.pi()

for i in range(2,27):
   pi.set_mode(i, pigpio.OUTPUT)

while 1:
   for i in range(2, 27):
      if pi.read(i) > 0:
         print pi.read(i)
   time.sleep(1)
