package com.github.cpzt.client;

import com.github.cpzt.common.Log;
import com.github.cpzt.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

public class Voter implements Runnable{

  private Socket socket;
  private final Set<Socket> socketSet;

  private final String ip;

  private final int port;

  private volatile boolean stop;

  private final String voteMessage;

  private final String commitMessage;

  private static Log log = new Log();

  public Voter(String ip, int port, Set<Socket> socketSet, String voteMessage, String commitMessage) {
    this.ip = ip;
    this.port = port;
    this.socketSet = socketSet;
    this.voteMessage = voteMessage;
    this.commitMessage = commitMessage;

    createSocket();
  }

  public Voter(String ip, int port, Set<Socket> socketSet) {
    this(ip, port, socketSet, Message.VOTE, Message.COMMIT);
  }


  public static Voter newVoter(String ip, int port, Set<Socket> socketSet) {
    return new Voter(ip, port, socketSet);
  }

  public static Voter newVoter(String ip, int port, Set<Socket> socketSet, String voteMessage, String commitMessage) {
    return new Voter(ip, port, socketSet, voteMessage, commitMessage);
  }

  public void close() {
    try {
      socketSet.remove(socket);
      stop = true;
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    new Thread(this).start();
  }

  public void createSocket() {
    try {
      socket = new Socket(ip, port);
      log.write(socket + " start!");
    } catch (IOException e) {
      socketSet.remove(socket);
      log.write(socket + "start failed!");
    }
  }

  public void run() {
    stop = false;
    Message msg = null;

    while (!stop) {
      msg = ReadMessage();
      handleMessage(msg);
    }
  }

  public Message ReadMessage() {
    try {
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      return (Message) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      close();
    }
    return null;
  }

  private void sendMessage(Message msg) {

    try {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(msg);
    } catch (IOException e) {
      close();
    }

  }

  public boolean handleMessage(Message msg) {
    String msgValue = null;
    if (msg == null || (msgValue = msg.getValue()) == null) {
      return false;
    }

    if (Message.START_2PC.equals(msgValue)) {
      sendMessage(Message.of(voteMessage));
    } else if (Message.START_COMMIT.equals(msgValue)) {
      sendMessage(Message.of(commitMessage));
    } else {
      return false;
    }
    return true;
  }
}
