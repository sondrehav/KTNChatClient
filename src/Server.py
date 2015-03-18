import socket, threading, time, sys

def printMsg(inputMsg, name):
    print('['+str(time.time())+'; '+str(name)+']:\t'+str(inputMsg))
    
HOST = 'localhost'
PORT = 6773
BUFSIZE = 1024
ADDR = (HOST, PORT)
SOCKETS = []

printMsg('Starting server on ' + str(HOST) + ':' + str(PORT) + '.', 'START')

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind(ADDR)
#serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serverSocket.listen(1)

SOCKETS.append(serverSocket)

printMsg('Successful.', 'START')

def recieveFunc():
    conn, addr = serverSocket.accept()
    printMsg('Connection from ' + str(addr)), 'CON')
    while(True):
        message = serverSocket.recv(BUFSIZE)
        printMsg(message, 'MSG')
    return

threading.Thread(target=recieveFunc).start()

while(True):
    data = input()
    if not data: break;

printMsg('Closing...', 'STOP')

serverSocket.close()