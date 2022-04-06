package com.github.cpzt.server;


import com.github.cpzt.client.Voter;
import sun.plugin.dom.core.CoreConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Coordinator {

  private final int port;

  private Map<Socket, Voter> socketToVoter;

  private ServerSocket serverSocket;

  private volatile boolean stop;

  public Coordinator(int port) {
    this.port = port;
    socketToVoter = new HashMap<Socket, Voter>();
  }

  public void startup() throws Exception {


  }


  private class ListenThread implements Runnable {

    public ListenThread() { }

    public void run() {
      try {
        serverSocket = new ServerSocket();

        stop = false;
        Socket socket = null;

        while (!stop) {
          socket = serverSocket.accept();
          socketToVoter.put(socket, new Voter(socket, socketToVoter));
        }
      } catch (IOException e) {
        close();
      }
    }

    public void close() {
      try {
        stop = true;
        serverSocket.close();
        System.out.println("Server Socket is Closed");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
