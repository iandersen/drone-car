import subprocess
import sys
 
ip = sys.argv[1]
    
# ping ip
p = subprocess.Popen(['ping', ip, '-c1'], stdout=subprocess.PIPE,
        stderr=subprocess.PIPE)
 
out, err = p.communicate()
 
# arp list
p = subprocess.Popen(['arp', '-n'], stdout=subprocess.PIPE,
        stderr=subprocess.PIPE)
 
out, err = p.communicate()
 
try:
    arp = [x for x in out.split('\n') if ip in x][0]
except IndexError:
    sys.exit(1)     # no arp entry found
else:
    # get the mac address from arp list
    # bug: when the IP does not exists on the local network
    # this will print out the interface name
    print ' '.join(arp.split()).split()[2]