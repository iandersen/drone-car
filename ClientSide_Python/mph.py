import time;
import obd;
import atexit;
import pymysql;
from gps3 import gps3;
import threading
print('Waiting 5 seconds to warm up!')
time.sleep(5);
print('Connecting to OBD2 diagnostics')

connection = obd.OBD("/dev/rfcomm0");

print('Connected!')

cmd = obd.commands.SPEED;
rpmCMD = obd.commands.RPM;
cmd = obd.commands;
myCommands = [];
myCommands.append(cmd.SPEED);
myCommands.append(cmd.RPM);
myCommands.append(cmd.ENGINE_LOAD)
myCommands.append(cmd.COOLANT_TEMP)
myCommands.append(cmd.INTAKE_TEMP)
myCommands.append(cmd.MAF);
myCommands.append(cmd.THROTTLE_POS);
myCommands.append(cmd.RUN_TIME);
myCommands.append(cmd.FUEL_LEVEL);
myCommands.append(cmd.BAROMETRIC_PRESSURE);
myCommands.append(cmd.RELATIVE_THROTTLE_POS);
extraCommands = 6;

fh = open("speed.txt", "a+");

columns = "speed, rpm, engine_load, coolant_temp, intake_temp, maf, throttle_pos, run_time, fuel_level, barometric_pressure, relative_throttle_pos, lat, lon, alt, time, gpsSpeed, climb";
strings = "%s,%s,%s,%s,%s,%s,";
for cmd in myCommands:
	strings += "%s,"
strings = strings[:-1];#remove trailing comma
db = pymysql.connect(host="localhost",user="root",db="car_data",autocommit=True);

sql = "INSERT INTO `speed_data`(" + columns + ") values(" + strings +")";
cursor = db.cursor()

lat = 0;
lon = 0;
alt = 0;
gpsTime = '0';
gpsSpeed = 0;
climb = 0;
hasConnectedToGPS = 0;

class GpsPoller(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
		self.socket = gps3.GPSDSocket()
		self.stream = gps3.DataStream()
		self.socket.connect()
		self.socket.watch()
		self.running = True
	def run(self):
		global lat
		global lon
		global alt
		global gpsTime
		global climb
		global gpsSpeed
		global hasConnectedToGPS
		for new_data in self.socket:
			if new_data:
				self.stream.unpack(new_data)
				lat = self.stream.TPV['lat']
				lon = self.stream.TPV['lon']
				alt = self.stream.TPV['alt']
				gpsTime = self.stream.TPV['time']
				gpsSpeed = self.stream.TPV['speed']
				climb = self.stream.TPV['climb']
				if hasConnectedToGPS == 0 and lat != 'n/a':
					hasConnectedToGPS = 1
					print('GPS Connection Established!')

				
gpsp = GpsPoller()
gpsp.start()

while 1:
	values = []
	for cmd in myCommands:
		resp = connection.query(cmd);
		if not resp.is_null():
			if(hasattr(resp.value, "magnitude")):
			#	print('Magnitude: ', str(resp.value.magnitude))
				values.append(float(resp.value.magnitude))
			else:
				values.append(float(resp));
	if lat != 'n/a':
		values.append(float(lat) if lat != 'n/a' else 0)
		values.append(float(lon) if lon != 'n/a' else 0)
		values.append(float(alt) if alt !='n/a' else 0)
		print('Lat: ' + str(lat) + ' Lon: ' + str(lon))
		values.append(gpsTime)
		values.append(float(gpsSpeed) if gpsSpeed !='n/a' else 0)
		values.append(float(climb) if climb !='n/a' else 0)
		if(len(values) == len(myCommands) + extraCommands):
			cursor.execute(sql,values);
	time.sleep(1);


def onExit():
	print("exiting");
	connection.close()
atexit.register(onExit)
