import socket, threading, sys, time

def printMsg(inputMsg, name):
    print('['+str(time.time())+'; '+str(name)+']:\t'+str(inputMsg))

HOST = input('[IP]:\t')
printMsg(HOST, 'CON')
PORT = 6773
BUFSIZE = 1024
ADDR = (HOST, PORT)

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.connect(ADDR)

login = input('[NAME]:\t')
clientSocket.send(login.encode('ascii'))
dt = clientSocket.recv(BUFSIZE)
printMsg(dt, 'RECV')

def recieveFunc():
    while(True):
        message = clientSocket.recv(BUFSIZE)
        if not message: sys.exit()
        print(printMsg(message, 'RECV'))

threading.Thread(target=recieveFunc).start()

while(True):
    inputMsg = input('[SEND]:\t')
    if not inputMsg: break
    clientSocket.send(inputMsg.encode('ascii'))
    
clientSocket.close()
