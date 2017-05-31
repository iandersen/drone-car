#! /usr/bin/env python

# grip 0.0.1, external IP address lookup utility.
# usage: grip hostname [port]
#        grip -l [hostname] [port]
#        grip -h

from __future__ import print_function

import sys
from socket import *

MODE_SERVER = 1
MODE_CLIENT = 0

READ_BUFFER = 15
NULL_BUFFER = 1

DEFAULT_HOST = ''
DEFAULT_PORT = 5001

EXIT_SUCCESS = 0
EXIT_FAILURE = 1
EXIT_SYNTAX  = 2

USAGE = '''\
grip 0.0.1, external IP address lookup utility.
usage: grip hostname [port]
       grip -l [hostname] [port]
       grip -h

Options:
  -h        display this help message and exit
  -l        run in listen mode, to report back remote hosts' IP addresses

Request mode:
  The hostname may be provided in either IP address or DNS format.
  The port number - if unspecified - defaults to 20815.

Listen mode:
  In listen mode the program will try to serve requests on all
  available interfaces unless explicitly told not to.\
'''

def num(n):
  try:
    return int(n)
  except ValueError:
    return None

class Server:
  """Reports back the remote host's IP address"""
  def __init__(self, socket):
    self.socket = socket

  def listen(self, host, port):
    self.socket.bind((host, port))

    try:
      while 1:
        data, address = self.socket.recvfrom(NULL_BUFFER)
        self.socket.sendto(address[0], address)
    except KeyboardInterrupt:
      pass

    return EXIT_SUCCESS

class Client:
  """xyz"""
  def __init__(self, socket):
    self.socket = socket

  def request(self, host, port):
    code = EXIT_SUCCESS

    self.socket.sendto('', (host,port))
    self.socket.settimeout(2)

    try:
      data, saddr = self.socket.recvfrom(READ_BUFFER)
      print(data)
    except:
      code = EXIT_FAILURE;
      print('Error: Request timed out', file=sys.stderr)

    self.socket.close()
    return code


def main():
  args   = list(sys.argv[1:])
  argsno = len(args)

  mode = MODE_CLIENT

  hostname = DEFAULT_HOST
  port = DEFAULT_PORT

  # Mere invocation is NOT supported
  if argsno == 0:
    print(USAGE, file=sys.stderr)
    sys.exit(EXIT_SYNTAX)

  # Display a helpfull message when asked
  if '-h' in args:
    print(USAGE)
    sys.exit(EXIT_SUCCESS)

  if '-l' in args:
    mode = MODE_SERVER

    args.remove('-l')
    argsno -= 1

  if mode == MODE_SERVER:
    if argsno > 1:
      hostname = args[0]
      port = num(args[1])

    if argsno == 1:
      if num(args[0]) is not None:
        port = num(args[0])
      else:
        hostname = args[0]

    try:
      serv = Server(socket(AF_INET, SOCK_DGRAM))
      retval = serv.listen(hostname, port)
    except:
      sys.exit(EXIT_FAILURE)

  if mode == MODE_CLIENT:
    hostname = args[0]

    if argsno > 1:
      port = num(args[1])

    try:
      cl = Client(socket(AF_INET, SOCK_DGRAM))
      retval = cl.request(hostname, port)
    except:
      sys.exit(EXIT_FAILURE)

  sys.exit(retval)

if __name__ == "__main__":
  main()