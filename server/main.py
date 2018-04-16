#!/bin/python3

import socket
from datetime import datetime
from threading import Thread, Lock
import json


devices_keys = ["methane", "smoke", "hydrogen", "propane_butane", "carbon", "fahrenheit", "cesium", "humidity"]

devices = {key: None for key in devices_keys}
lock = Lock()

def create_server_socket(host_name: str, port: int, backlog: int) -> socket.socket:
    server_socket = socket.socket()
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind((host_name, port))
    server_socket.listen(backlog)

    return server_socket

def devices_task():
    devices_server_socket = create_server_socket("localhost", 9090, 1)

    while True:
        devices_client_socket, addr = devices_server_socket.accept()

        if devices_client_socket.recv(10) != b"amdevice\r\n":
            devices_client_socket.send(b"you are unable to connect\n")
            devices_client_socket.close()
            continue

        global devices
        global lock

        while True:
            device_information = devices_client_socket.recv(42).decode("utf-8").strip()
            print(device_information, addr)
            str_values = [value for value in device_information.split(",")]
            print(str_values)
            values = [int(value) for value in str_values[0:5]] + [float(value) for value in str_values[5:]]
            print(values)
            with lock:
                for i, key in enumerate(devices_keys):
                    print(i, key)
                    devices[key] = values[i]
                print(json.dumps(devices, indent=4))

def clients_task():
    global lock
    client_server_socket = create_server_socket("localhost", 8080, 10)

    while True:
        client_socket, _ = client_server_socket.accept()

        with lock:
            client_socket.send(json.dumps(devices).encode())

        client_socket.close()


def main():
    devices_thread = Thread(target=devices_task)
    devices_thread.start()
    client_thread = Thread(target=clients_task)
    client_thread.start()


if __name__ == "__main__":
    main()
