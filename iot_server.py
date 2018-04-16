#!/bin/python3

import json
import socket
from datetime import datetime
from threading import Thread, Lock

class Device(object):
    def __init__(self, keys: list):
        self._device_keys = keys
        self._info = {key: None for key in self._device_keys}


    def set_values(self, values: list):
        for i, key in enumerate(self._device_keys):
            self._info[key] = int(values[i]) if i < 5 else values[i]


    def __str__(self):
        return json.dumps(self._info)



class Server(object):
    def __init__(self, host, port, backlog):
        self._server_socket = Server._create_server_socket(host, port, backlog)
        self._continue_task_execution = True
        self._thread = None # must be init in subclasses


    def start(self):
        self._thread.start()


    def end(self):
        self._continue_task_execution = False
        self._thread.join()


    def _server_task(self):
        raise NotImplementedError


    @staticmethod
    def _create_server_socket(host: str="", port: int=8888, backlog: int=10) -> socket.socket:
        server_socket = socket.socket()
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind((host, port))
        server_socket.listen(backlog)

        return server_socket



class DevicesServer(Server):
    def __init__(self, device: Device, server_lock: Lock, host: str="localhost", port: int=16132, backlog: int=1):
        Server.__init__(self, host, port, backlog)
        self._device = device
        self._server_lock = server_lock
        self._thread = Thread(target=self._server_task)


    def _was_server_started(self):
        return True if not self._thread.is_alive() else False


    def _get_device_client(self, check_word: str, error_phrase: str):
        while True:
            client_socket, client_addr = self._server_socket.accept()

            client_message = client_socket.recv(len(check_word)).decode("utf-8")

            if client_message != check_word:
                client_socket.send(error_phrase.encode())
                client_socket.close()
                continue

            self._devices_client_socket = client_socket
            self._devices_client_addr = client_addr
            return


    def _server_task(self):
        self._get_device_client("amdevice\r\n", "you are unable to connect\r\n")
        print("devise info publisher connected from {} on {} on {}".format(*self._devices_client_addr, datetime.now()))

        while self._continue_task_execution:
            device_information = self._devices_client_socket.recv(40).decode("utf-8").strip()
            str_values = [value.strip() for value in device_information.split(",")]
            values = [float(value) for value in str_values]
            with self._server_lock:
                self._device.set_values(values)

            print("devise info publisher send info update '{}' on {}\nnew device info: {}".format(values, datetime.now(), str(self._device)))

        self._devices_client_socket.close()


class ClientServer(Server):
    def __init__(self, device: Device, server_lock: Lock, host: str="localhost", port: int=13162, backlog: int=5):
        Server.__init__(self, host, port, backlog)
        self._device = device
        self._server_lock = server_lock
        self._thread = Thread(target=self._server_task)


    def _server_task(self):
        while self._continue_task_execution:
            client_socket, client_addr = self._server_socket.accept()
            client_message = str(self._device)
            print("client connected from {} on {} on {}\nsending info: {}".format(*client_addr, datetime.now(), client_message))

            with self._server_lock:
                client_socket.send(client_message.encode())

            client_socket.close()


def main():
    try:
        device = Device(["methane", "smoke", "hydrogen", "propane_butane", "carbon", "fahrenheit", "cesium", "humidity"])
        lock = Lock()
        servers = [DevicesServer(device, lock),
                    ClientServer(device, lock)]
        for server in servers:
            server.start()
    except KeyboardInterrupt:
        for server in servers:
            server.end()


if __name__ == "__main__":
    main()
