import serial
import socket
from time import sleep
ser = serial.Serial(port='/dev/ttyACM0',
                    baudrate=9600,
                    parity=serial.PARITY_NONE,
                    stopbits=serial.STOPBITS_ONE,
                    bytesize=serial.EIGHTBITS,
                    timeout=10)

client = socket.socket()
# TODO:
ip = "10.11.162.203"
port = 16132
client.connect((ip, port))
client.sendall(b'amdevice\r\n')

while True:
    answ = ser.readline()
    print(answ)
    #answ  = bytes(answ, "utf8")
    client.sendall(answ)
    sleep(0.5)
client.close()
