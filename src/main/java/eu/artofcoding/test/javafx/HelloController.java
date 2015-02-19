package eu.artofcoding.test.javafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class HelloController {

    private Stage stage;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField stringTextField;

    @FXML
    private SimpleStringProperty string = new SimpleStringProperty();

    @FXML
    private Button showFileChooser;

    @FXML
    public void initialize() {
        string.bind(stringTextField.textProperty());
        stringTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                System.out.println(observableValue + ": " + s + " -> " + s2);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
    }

    public String getString() {
        return string.get();
    }

    public SimpleStringProperty stringProperty() {
        return string;
    }

    public void setString(String string) {
        this.string.set(string);
    }

    public void sayHello() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(firstName)) {
            builder.append(firstName);
        }
        if (!StringUtils.isEmpty(lastName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName);
        }
        if (builder.length() > 0) {
            String name = builder.toString();
            messageLabel.setText("Hello " + name);
        } else {
            messageLabel.setText("Hello mysterious person");
        }
    }

}
