package vars.queryfx.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Brian Schlining
 * @since 2015-09-30T11:24:00
 */
public class DatePickerDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 30, 30, 30));
        LocalDateTimeTextField tf = new LocalDateTimeTextField(LocalDateTime.now());
        tf.parseErrorCallbackProperty().set( p -> {
            tf.setLocalDateTime(LocalDateTime.now());
            return null;
        });

        tf.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME);
        tf.setLocale(Locale.ENGLISH);
        tf.localDateTimeProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("New date: " + newValue);
        });
        tf.setAllowNull(false);


        root.getChildren().add(tf);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
