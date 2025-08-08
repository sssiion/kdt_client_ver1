package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class M_MainController {
    @FXML private VBox topBarContainer;
    @FXML private AnchorPane sideBarContainer;
    @FXML private StackPane stackPane;

    private SidebarController sidebarController;

    @FXML
    public void initialize() throws IOException {
        // 상단 바 로딩 후 붙이기
        FXMLLoader topBarLoader = new FXMLLoader(getClass().getResource("mtop_bar.fxml"));
        Parent topBar = topBarLoader.load();
        topBarContainer.getChildren().add(topBar);

        MTopBarController topBarController = topBarLoader.getController();
        topBarController.setMainController(this);

        // 사이드바 로딩 후 붙이기(좌측 AnchorPane 내부)
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("sidebar.fxml"));
        Parent sidebar = sidebarLoader.load();
        sideBarContainer.getChildren().add(sidebar);

        sidebarController = sidebarLoader.getController();
        sidebarController.setMainController(this);

        // 초기 화면 로딩 후 스택페인에 붙이기
        FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("M_home.fxml"));
        Parent mainView = mainViewLoader.load();
        stackPane.getChildren().setAll(mainView);
    }

    public void showScreen(Parent screen) {
        stackPane.getChildren().setAll(screen);
    }
}
