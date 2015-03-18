import socket, threading, sys

HOST = input('IP \t> ')
PORT = 6773
BUFSIZE = 1024
ADDR = (HOST, PORT)

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.connect(ADDR)

login = input('SEND > ')
clientSocket.send(login)

def recieveFunc():
    while(True):
        message = clientSocket.recv(BUFSIZE)
        if not message: sys.exit()
        print('MSG \t> ' + message)

threading.Thread(target=recieveFunc).start()

while(True):
    inputMsg = input('> ')
    if not inputMsg: break
    clientSocket.send(inputMsg)
    
clientSocket.close()