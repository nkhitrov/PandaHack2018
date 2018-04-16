import datetime
import socket
from threading import Thread, Lock
import threading
import json

devices_keys = ["methane", "smoke", "hydrogen", "propane_butane", "carbon", "fahrenheit", "celsium", "humidity"]

devices = {key: None for key in devices_keys}
lock = Lock()

def create_server_socket(host_name: str, port: int, backlog: int) -> socket.socket:
    server_socket = socket.socket()
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind((host_name, port))
    server_socket.listen(backlog)

    return server_socket

def devices_task():
    global devices
    global lock
    server_socket = create_server_socket('', 16132, 5)

    while True:
        devices_client_socket, addr = server_socket.accept()

        if devices_client_socket.recv(10) != b"amdevice\r\n":
            devices_client_socket.send(b"you are unable to connect\n")
            devices_client_socket.close()
            continue

        while True:
            device_information = devices_client_socket.recv(1024).decode("utf-8").strip()
            print("message from devices on {}:".format(datetime.datetime.now(), *addr), device_information)
            str_values = [value for value in device_information.split(",")]
            values = [float(value) for value in str_values]
            for i, key in enumerate(devices_keys):
                devices[key] = values[i]
            print(json.dumps(devices))


def client_task():
    global lock
    server_socket = create_server_socket('', 13162, 5)

    while True:
        try:
            global devices
            client_socket, client_addr = server_socket.accept()
            send_message = json.dumps(devices)
            print("{} => message from server to {} on {}:".format(datetime.datetime.now(), *client_addr), send_message)
            client_socket.sendall(send_message.encode())
            client_socket.close()
        except ConnectionResetError as e:
            print("{} error: ".format(client_addr), e.strerror)


def main():

    print("server started")

    t1 = threading.Thread(target=devices_task)
    t2 = threading.Thread(target=client_task)

    t1.start()
    t2.start()

    t1.join()
    t2.join()

if __name__ == "__main__":
    main()
