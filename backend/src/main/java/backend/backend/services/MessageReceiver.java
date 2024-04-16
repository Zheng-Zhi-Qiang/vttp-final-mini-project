package backend.backend.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.backend.models.Message;
import jakarta.json.Json;

@Component
public class MessageReceiver {

  @Autowired
  private ConversationService convoSvc;

  public void receiveMessage(String message) {
    System.out.println("Received <" + message + ">");
    // Message msg = Message.toMessage(Json.createReader(new
    // StringReader(message)).readObject());
  }
}
