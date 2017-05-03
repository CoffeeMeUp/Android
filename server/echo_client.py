#!/usr/bin/python
# Demo client program
# Modified from Python tutorial docs
import socket

HOST = '192.168.43.189'  # Set the remote host, for testing it is localhost
PORT = 5000              # The same port as used by the server
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))
s.send('Where there is love there is life')
data = s.recv(1024)
s.close()
print 'Received', repr(data)
