#!/usr/bin/python
# Demo server to open port 8888
# Modified from Python tutorial docs
import socket

HOST = '192.168.43.189'       # Hostname to bind
PORT = 6666              # Open non-privileged port 8888

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(1)
while True:
    conn, addr = s.accept()
    print 'Connected by', addr
    while True:
        data = conn.recv(1024)
        if not data:
            break
        print repr(data)
        # conn.send(data)
    conn.close()
    if data == 'close':
        break
