import socket, threading, time, sys

HOST = "localhost"
PORT = 6773
BUFSIZE = 1024
ADDR = (HOST, PORT)
SOCKETS = []

print "Starting server on (" + str(HOST) + ", " + str(PORT) + ")" + "..."

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind(ADDR)
serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serverSocket.listen(1)

SOCKETS.append(serverSocket)

print "Server bound on " + str(socket.gethostname())



def recieveFunc():
    conn, addr = serverSocket.accept()
    print 'hello ' + str(addr)
    while(True):
        message = serverSocket.recv(BUFSIZE)
        print "[" + str(time.time()) + "]: " + message
    return

threading.Thread(target=recieveFunc).start()

while(True):
    data = input()
    if not data: break;

print "Closing server..."

serverSocket.close()