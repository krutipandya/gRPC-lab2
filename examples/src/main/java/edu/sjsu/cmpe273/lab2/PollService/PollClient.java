package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link PollServer}.
 */
public class PollClient {
  private static final Logger logger = Logger.getLogger(PollClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void create(String moderatorId,String question,String startedAt,String expiredAt,String[] choice) {
    try {
      logger.info("Moderator " + moderatorId + " ...");
     
      PollRequest request = PollRequest.newBuilder()
      .setModeratorId(moderatorId)
      .setQuestion(question)
      .setStartedAt(startedAt)
      .setExpiredAt(expiredAt)
      .addChoice(choice[0])
      .addChoice(choice[1]).build();
      
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Poll Id : " + response.getId());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    PollClient client = new PollClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String moderatorId = "1";
      String question = "What type of smartphone do you have?";
      String started_at = "2015-02-23T13:00:00.000Z";
      String expired_at = "2015-02-24T13:00:00.000Z";
      String[] choice = new String[] { "Android", "iPhone" };

      client.create(moderatorId,question,started_at,expired_at,choice);
    } finally {
      client.shutdown();
    }
  }
}
