package com.github.cpzt;


import com.github.cpzt.client.Voter;
import com.github.cpzt.common.Message;
import com.github.cpzt.server.Coordinator;
import org.junit.Test;


public class TwoPCTest {

  @Test
  public void abortTest() throws Exception {
    String hostname = "localhost";
    int port = 8023;
    Coordinator coordinator = new Coordinator(port);
    coordinator.preStart();

    Voter voter = Voter.newVoter(hostname, port, coordinator.getSocketSet(), Message.VOTE, Message.COMMIT);
    voter.start();

    Voter voter1 = Voter.newVoter(hostname, port, coordinator.getSocketSet(), Message.VOTE, Message.COMMIT_ABORT);
    voter1.start();

    coordinator.start();

  }

  @Test
  public void voteTest() throws Exception {
    String hostname = "localhost";
    int port = 8023;
    Coordinator coordinator = new Coordinator(port);
    coordinator.preStart();

    Voter voter = Voter.newVoter(hostname, port, coordinator.getSocketSet());
    voter.start();

    Voter voter1 = Voter.newVoter(hostname, port, coordinator.getSocketSet());
    voter1.start();

    coordinator.start();
  }

}
