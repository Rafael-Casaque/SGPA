package com.example.sgpa.application.controller;

import com.example.sgpa.application.repository.sqlite.*;
import com.example.sgpa.application.view.WindowLoader;
import com.example.sgpa.domain.usecases.checkout.CheckOutDAO;
import com.example.sgpa.domain.usecases.checkout.CheckedOutItemDAO;
import com.example.sgpa.domain.usecases.checkout.ReturnPartItemUseCase;
import com.example.sgpa.domain.usecases.historical.EventDAO;
import com.example.sgpa.domain.usecases.part.CheckForPartItemAvailabilityUseCase;
import com.example.sgpa.domain.usecases.part.PartItemDAO;
import com.example.sgpa.domain.usecases.user.CheckForUserPendingIssuesUseCase;
import com.example.sgpa.domain.usecases.user.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ReturnUIController {
    @FXML
    private Button btnBack;
    @FXML
    private Button btnConclude;
    @FXML
    private Label lblReturnedPartItem;
    @FXML
    private TextField txtPatrimonialId;
    private final UserDAO userDAO = new SqliteUserDAO();
    private final PartItemDAO partItemDAO =  new SqlitePartItemDAO();
    private final CheckedOutItemDAO checkedOutItemDAO = new SqliteCheckedOutItemDAO();
    private final EventDAO eventDAO = new SqliteEventDAO();
    ReturnPartItemUseCase returnPartItemUseCase = new ReturnPartItemUseCase(checkedOutItemDAO,partItemDAO, eventDAO, userDAO);

    @FXML
    void backToPreviousScene(ActionEvent event) throws IOException {
        WindowLoader.setRoot("MainUI.fxml");
    }
    @FXML
    void conclude(ActionEvent event) {
        if (!txtPatrimonialId.getText().isEmpty()){
            try {
                returnPartItemUseCase.returnPartItem(Integer.parseInt(txtPatrimonialId.getText()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("SGPA informa");
                alert.setContentText("Peça de patrimônio nº "+txtPatrimonialId.getText()+" devolvida com sucesso!");
                alert.showAndWait();
                txtPatrimonialId.clear();
            }catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("SGPA informa");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
