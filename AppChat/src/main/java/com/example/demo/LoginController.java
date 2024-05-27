package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class LoginController {
    @FXML
    private Button signup_btn;

    @FXML
    private TextField su_email;

    @FXML
    private TextField su_password;

    @FXML
    private TextField su_username;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private String username;

    private Client client;

    @FXML
    public void initialize() {
    }

    public void loginButtonOnAction(ActionEvent e) {
        if (!usernameTextField.getText().isBlank() && !passwordPasswordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Vui lòng nhập tài khoản và mật khẩu!");
        }
    }

    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        DatabaseConnection connecNow = new DatabaseConnection();
        Connection connectDb = connecNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM useraccounts WHERE UserName = ? AND Password = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(verifyLogin)) {
            preparedStatement.setString(1, Encrypt.encoded(usernameTextField.getText()));
            preparedStatement.setString(2, Encrypt.encoded(passwordPasswordField.getText()));

            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next() && queryResult.getInt(1) == 1) {
                username = usernameTextField.getText();
                openViewMenuWindow();
            } else {
                loginMessageLabel.setText("Sai tài khoản hoặc mật khẩu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openViewMenuWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ViewMenu.fxml"));
            Parent root = fxmlLoader.load();

            Client clientController = fxmlLoader.getController();
            clientController.setUsername(username);

            Stage stage = new Stage();
            stage.setTitle("Welcome");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnSever(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chat App");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateAccount(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAccount.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create Account");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signup() {
        String sqlCheck = "SELECT UserName FROM useraccounts WHERE Email = ?";
        String sqlInsert = "INSERT INTO useraccounts (UserName, Password, Email) VALUES (?, ?, ?)";

        Connection connect = null;
        PreparedStatement prepareCheck = null;
        PreparedStatement prepareInsert = null;
        ResultSet result = null;

        try {
            connect = DatabaseConnection.getConnection();

            prepareCheck = connect.prepareStatement(sqlCheck);
            prepareCheck.setString(1, su_email.getText());
            result = prepareCheck.executeQuery();

            if (result.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Email đã tồn tại!");
                alert.showAndWait();
            } else {
                prepareInsert = connect.prepareStatement(sqlInsert);
                prepareInsert.setString(1, Encrypt.encoded(su_username.getText()));
                prepareInsert.setString(2, Encrypt.encoded(su_password.getText()));
                prepareInsert.setString(3, su_email.getText());

                if (su_username.getText().isEmpty() || su_password.getText().isEmpty() || su_email.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Vui lòng điền đầy đủ thông tin");
                    alert.showAndWait();
                } else {
                    int rowsAffected = prepareInsert.executeUpdate();
                    if (rowsAffected == 1) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText(null);
                        alert.setContentText("Tạo tài khoản thành công!");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText(null);
                        alert.setContentText("Tạo tài khoản thất bại!");
                        alert.showAndWait();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (prepareCheck != null) {
                    prepareCheck.close();
                }
                if (prepareInsert != null) {
                    prepareInsert.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}