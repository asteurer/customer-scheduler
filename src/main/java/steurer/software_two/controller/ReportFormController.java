package steurer.software_two.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import steurer.software_two.helper.Utilities;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReportFormController implements Initializable {

    @FXML
    private Label reportTitleLbl;
    @FXML
    private Label reportTextLbl;
    @FXML
    private Button cancelBtn;

    /**
     * Redirects to the MainMenuForm without a confirmation message.
     */
    @FXML
    private void onActionCancelBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = Utilities.getLoader("MainMenuForm");
        Utilities.setStage(loader, event, "MainMenuForm", true);
    }

    /**
     * This allows for report data for multiple reports generated on the MainMenuFormController to be displayed here.
     */
    public void createReport(String titleBundleLabel, String reportText) {
        reportTitleLbl.setText(Utilities.translate(titleBundleLabel));
        reportTextLbl.setText(reportText);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelBtn.setText(Utilities.translate("goBack"));
    }
}
