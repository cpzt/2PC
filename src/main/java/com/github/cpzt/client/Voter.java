package com.github.cpzt.client;


import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Voter {
  private final Socket socket;
  private final Map<Socket, Voter> socketToVoter;

  public Voter(Socket socket, Map<Socket, Voter> socketToVoter) {
    this.socket = socket;
    this.socketToVoter = socketToVoter;
  }

  public void close() {
    try {
      socketToVoter.remove(socket);
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
