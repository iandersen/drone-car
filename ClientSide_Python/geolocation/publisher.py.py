import urllib2, time

def publishtoInternet(temp):
    url = "http://data.sparkfun.com/input/dZa421pxLOhAVJKE1gYQ?private_key=<your_key>&temp=" + temp
    try:
        print url
        result = urllib2.urlopen(url).read()
        print result+ str(temp)
    except:
        print 'exception uploading, logging to file'
        log(temp)

def log(temp):
    f = open('log','a')
    t = time.localtime(time.time())
    msg =str(t.tm_hour)+' ' + str(t.tm_min)
    f.write(msg)
    f.write('temp: ')
    f.write(temp)
    
    f.write('\n')
    f.close()
