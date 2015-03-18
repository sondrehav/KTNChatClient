import socket, threading, sys

HOST = "localhost"
PORT = 6774
BUFSIZE = 1024
ADDR = (HOST, PORT)

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.connect(ADDR)

print "Nickname: "
login = raw_input()
clientSocket.sendall(login)

def recieveFunc():
    while(True):
        message = clientSocket.recv(BUFSIZE)
        if not message: sys.exit()
        print message

threading.Thread(target=recieveFunc).start()

while(True):
    input = raw_input('> ')
    if not input: break
    clientSocket.send(input)
    
clientSocket.close()