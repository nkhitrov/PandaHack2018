import socket

def main():
    client_socket = socket.socket()
    client_socket.connect(("", 16132))
    print("message from server:", client_socket.recv(200).decode("utf-8"))
    client_socket.send(b"test message from client")
    client_socket.close()

if __name__ == "__main__":
    main()
