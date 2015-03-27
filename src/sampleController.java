/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Junior
 */
public class sampleController {
    static final String HELP_TEXT = "Help Text\n Here";
    static final String ABOUT_TEXT = "About Us Text\n Here";
    static final String CONTACT_TEXT = "Contact Text\n Here";
    static final String CONSUMERKEY = "LjVW0tPa9PiWeoI7SOuMLI2S6";
    static final String CONSUMERSECRET = "rytpeLKW9Ri4BcAtrytNSuZS6nwPQcua0zNmWNSeGLF1W2UDYl";
    static final String TOKENKEY = "2181560922-KoZRFjTWnonPDiEfm46896BzHziOHTgaZSKtwOg";
    static final String TOKENSECRET = "UQD3XZAnDZGYVPlwz8hKrNYXTfZAGwxXKrB0URBlw4eeJ";
    static final String outputTxtFileName = "output.txt";
    static final String TWEETSEPARATOR = "\n_____________________________________________________\n";
    @FXML
    MenuBar menuBar;
    @FXML
    TextField flagTermTextField, fileNameTextField, usernameTextField, recipientEmailTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    CheckBox sendEmailCheckBox;
    @FXML
    TextArea consoleTextArea;
    //This boolean is used to check if already the start button has been clicked and is running. 
    //This ensures that we just have one instance of the search thread running.
    static boolean isRunning = false;

    //Event handler for the Start button click
    @FXML
    private void onStartButtonActionPerformed(ActionEvent event) {
        System.out.println("You clicked me!");
        //Create a runnable object for handling event outside of the UI thread
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    //Set isRunning to true..
                    isRunning = true;
                    String searchTerm = flagTermTextField.getText();
                    String fileName = fileNameTextField.getText();
                    //Check if filename length is > 0
                    if (fileName.length() == 0) {
                        appendConsoleTextAreaText("You must enter a valid file name");
                        return;
                    }
                    //Check if search term or flag term is not empty.
                    if (searchTerm.length() > 0) {
                        //Get a twitter object from TwitterUtil class
                        Twitter twitter = TwitterUtil.getTwitterInstance(CONSUMERKEY, CONSUMERSECRET, TOKENKEY, TOKENSECRET);
                        appendConsoleTextAreaText("Fetching search results for: " + searchTerm);
                        //Get tweets matching search term in statuses list from TwitterUtils class
                        List<Status> statuses = TwitterUtil.searchStatuses(searchTerm, twitter);
                        appendConsoleTextAreaText("Fetched Search Results: " + statuses.size());
                        appendConsoleTextAreaText("Now Writing results to file: ");
                        //iterate through all statuses and append the tweet to file.
                        for (Status s : statuses) {
                            FileIOUtils.appendTextToFile(fileName, s.getText());
                            FileIOUtils.appendTextToFile(fileName, TWEETSEPARATOR);
                        }
                        appendConsoleTextAreaText("File written successfully: " + fileName);
                        //Send email code
                        if (sendEmailCheckBox.isSelected()) {
                            appendConsoleTextAreaText("Now attempting to send email: ");
                            //Set credentials
                            String username = usernameTextField.getText();
                            String password = passwordField.getText();
                            String recipientId = recipientEmailTextField.getText();
                            if (username.length() == 0 || password.length() == 0 || recipientId.length() == 0) {
                                appendConsoleTextAreaText("Invalid entries to email.Please check again!!");
                            } else {
                                //Send emails to the recipient with attachment.
                                boolean sent = MailUtils.sendGmail(username, password, recipientId, fileName);
                                if (sent) {
                                    appendConsoleTextAreaText("Email Sent Successfully!");
                                } else {
                                    appendConsoleTextAreaText("Email sending failed. Please check your internet connections and/or credentials");
                                }
                            }
                        }
                    } else {
                        appendConsoleTextAreaText("You must enter a valid flag term!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isRunning = false;
                }
            }
        };
        if (!isRunning) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.start();
        } else {
            appendConsoleTextAreaText("Already Running!!");
        }
    }

    //This function is used to update the console text are
    private void appendConsoleTextAreaText(final String s) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                String text = consoleTextArea.getText();
                if (text.length() > 50000) {
                    text = "";
                }
                consoleTextArea.setText(text + "\n" + s);
            }
        });
    }

    public void handleBrowseMenuAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File f = chooser.showOpenDialog(Main.stage);
        if (f != null) {
            try {
                Desktop.getDesktop().open(f);
            } catch (IOException e) {
                appendConsoleTextAreaText("Exception opening file " + f.getName() + ":" + e.getMessage());
            }
        }
    }

    public void handleCloseMenuAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleHelpMenuAction(ActionEvent actionEvent) {
        showDialog(HELP_TEXT);
    }

    public void showDialog(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(VBoxBuilder.create().
                children(new Text(message)).
                alignment(Pos.CENTER).padding(new Insets(25, 50, 25, 50)).build()));
        dialogStage.show();
    }

    public void onAboutUsMenuActionPerformed(ActionEvent actionEvent) {
        showDialog(ABOUT_TEXT);
    }

    public void onContactUsMenuActionPerformed(ActionEvent actionEvent) {
        showDialog(CONTACT_TEXT);
    }
}

