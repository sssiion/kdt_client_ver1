package org.example.kdt_bank_client2.UserBank;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import org.example.kdt_bank_client2.UnifiedApplication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SidebarController {

    @FXML private Button menu1Header, menu2Header, menu3Header, menu4Header, menu5Header;
    @FXML private VBox menu1Content, menu2Content, menu3Content, menu4Content, menu5Content;
    // mainContentContainer 제거 (메인 컨트롤러가 담당)

    // M_MainController 참조
    private M_MainController mainController;

    public void setMainController(M_MainController mainController) {
        this.mainController = mainController;
    }

    // 화면 전환 메서드 변경: M_MainController의 showScreen() 호출
    public void loaderController(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            loader.setControllerFactory(UnifiedApplication.springContext::getBean);
            Parent content = loader.load();
            if (mainController != null) {
                mainController.showScreen(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void customerAdd(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_NewMember.fxml");
    }
    @FXML
    private void customerSearch(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_MemberInquiry.fxml");
    }

    @FXML
    private void customerInfoChange(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_MemberChange.fxml");
    }

    @FXML
    private void newAccountRegistration(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/newAccountRegistration.fxml");
    }

    @FXML
    private void productTermination(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/productTermination.fxml");
    }

    @FXML
    private void cashDeposit(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_Cashinandout.fxml");
    }

    @FXML
    private void accountTransfer(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_AccountTransfer.fxml");
    }

//    @FXML
//    private void withdrawalLimitSetting(ActionEvent event) {
//        loaderController("withdrawalLimitSetting.fxml");
//    }

    @FXML
    private void loanApplication(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/loanApplication.fxml");
    }

    @FXML
    private void contractChange(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/contractChange.fxml");
    }

    @FXML
    private void repaymentManagement(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/repaymentManagement.fxml");
    }


    @FXML
    private void contractManagement(ActionEvent event) {
        loaderController("/org/example/kdt_bank_client2/M_Agreement.fxml");
    }

    @FXML
    private void maturityProductCheck(ActionEvent event) { loaderController("/org/example/kdt_bank_client2/M_Expiration.fxml");}

    // 메뉴 접고 펼침 관리 (변경 없음)
    private void toggleMenuVisibility(VBox menuContent, Button headerButton) {
        if (menuContent.isVisible()) {
            menuContent.setVisible(false);
            menuContent.setManaged(false);
            headerButton.setText(headerButton.getText().replace("▼", "▶"));
        } else {
            menuContent.setVisible(true);
            menuContent.setManaged(true);
            headerButton.setText(headerButton.getText().replace("▶", "▼"));
        }
    }

    @FXML private void toggleMenu1() { toggleMenuVisibility(menu1Content, menu1Header); }
    @FXML private void toggleMenu2() { toggleMenuVisibility(menu2Content, menu2Header); }
    @FXML private void toggleMenu3() { toggleMenuVisibility(menu3Content, menu3Header); }
    @FXML private void toggleMenu4() { toggleMenuVisibility(menu4Content, menu4Header); }
    @FXML private void toggleMenu5() { toggleMenuVisibility(menu5Content, menu5Header); }


}
