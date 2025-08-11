package org.example.kdt_bank_client2;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Chatapp implements ApplicationListener<ChatClientApp.StageReadyEvent> {
    @Override
    public void onApplicationEvent(ChatClientApp.StageReadyEvent event) {
        Stage stage = event.getStage();

        VBox root = new VBox(new Label("채팅 시스템"));
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}
