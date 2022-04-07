package com.github.cpzt.server;


import com.github.cpzt.common.Log;
import com.github.cpzt.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Coordinator {

  private final int port;

  private final Set<Socket> socketSet;

  private ServerSocket serverSocket;

  private volatile boolean stop;

  private static final int WAIT_TIME_MILL_SECONDS = 1000;

  private Log log = new Log();

  public Coordinator(int port) {
    this.port = port;
    this.socketSet = new HashSet<>();
  }

  public Set<Socket> getSocketSet() {
    return this.socketSet;
  }

  public void preStart() throws Exception {
    // start listen thread and add client socket
    new Thread(new ListenThread(port, socketSet), "ListenThread").start();
  }

  public void start() throws InterruptedException {

    // Propose
    sendMessage(Message.of(Message.START_2PC));

    Thread.sleep(WAIT_TIME_MILL_SECONDS);

    if (!successForReadMessages(Message.VOTE)) {
      log.write("Vote Abort");
      return;
    }

    // Commit
    sendMessage(Message.of(Message.START_COMMIT));

    if(!successForReadMessages(Message.COMMIT)) {
     log.write("Commit Abort");
      return;
    }

    log.write("2PC success!");

    close();

  }

  private void close() {
    stop = true;
    try {
      serverSocket.close();
    } catch (IOException e) {
      log.write("server close failed");
    }
  }

  private boolean successForReadMessages(String msg) {
    boolean result = true;
    Message readMsg = null;
    for (Socket socket : socketSet) {
      if ((readMsg = readMessage(socket)) == null || !readMsg.getValue().equals(msg)) {
        log.write(socket + " ABORT");
        result = false;
        break;
      } else {
        log.write(socket + " " + readMsg.getValue());
      }
    }

    return result;
  }

  private Message readMessage(Socket socket) {
    try {
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      return (Message) ois.readObject();
    } catch (IOException | ClassNotFoundException ignored) {

    }
    return null;
  }

  private boolean sendMessage(Message msg) {

    for (Socket socket : socketSet) {
      try {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(msg);
      } catch (IOException e) {

        return false;
      }
    }
    return true;
  }


  private class ListenThread implements Runnable {

    private final int port;

    private final Set<Socket> socketSet;

    public ListenThread(int port, Set<Socket> socketSet) {
      this.port = port;
      this.socketSet = socketSet;
    }

    public void run() {
      try {
        serverSocket = new ServerSocket(port);

        Socket socket = null;

        while (!stop) {
          socket = serverSocket.accept();
          socketSet.add(socket);
          log.write("Add " + socket.toString());
        }
      } catch (IOException e) {
        close();
      }
    }

    public void close() {
      try {
        stop = true;
        serverSocket.close();
        log.write("Server Socket is Closed");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
