package vars.queryfx.ui;

import com.guigarage.sdk.util.MaterialDesignButton;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import jfxtras.scene.control.LocalDateTimePicker;
import jfxtras.scene.control.LocalDateTimeTextField;
import vars.queryfx.Lookup;
import vars.queryfx.ui.db.DateBoundsConstraint;
import vars.queryfx.ui.db.IConstraint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-28T11:28:00
 */
public class DateValuePanel extends AbstractValuePanel {

    private LocalDateTimeTextField startControl;
    private LocalDateTimeTextField endControl;
    private ChangeListener<LocalDateTime> changeListener = (obs, oldVal, newVal) ->
            getConstrainCheckBox().setSelected(!oldVal.isEqual(newVal));
    private Button scanButton = new MaterialDesignButton("Scan");


    public DateValuePanel(String valueName) {
        super(valueName);
        Region spacer = new Region();
        spacer.setMinWidth(20);
        HBox.setHgrow(spacer, Priority.NEVER);
        Region btnSpacer = new Region();
        btnSpacer.setMinWidth(10);
        HBox.setHgrow(btnSpacer, Priority.NEVER);
        getChildren().addAll(getStartControl(), spacer, getEndControl(), btnSpacer, scanButton);
        getConstrainCheckBox().setSelected(false);
    }

    private LocalDateTimeTextField getEndControl() {
        if (endControl == null) {
            LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            endControl = new LocalDateTimeTextField(dt);
            endControl.parseErrorCallbackProperty().set(p -> {
                endControl.setLocalDateTime(dt);
                return null;
            });
            endControl.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME);
            endControl.setId("date-field");
            HBox.setHgrow(endControl, Priority.ALWAYS);

            endControl.localDateTimeProperty().addListener(changeListener);
        }
        return endControl;
    }

    private LocalDateTimeTextField getStartControl() {
        if (startControl == null) {
            LocalDateTime dt = Lookup.getAnnotationStartDate().toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
            startControl = new LocalDateTimeTextField(dt);
            startControl.parseErrorCallbackProperty().set( p -> {
                startControl.setLocalDateTime(dt);
                return null;
            });
            startControl.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME);
            startControl.setId("date-field");
            HBox.setHgrow(startControl, Priority.ALWAYS);
            startControl.localDateTimeProperty().addListener(changeListener);

        }
        return startControl;
    }

    public ZonedDateTime getStartDate() {
        LocalDateTime local = getStartControl().getLocalDateTime();
        return ZonedDateTime.of(local, ZoneId.of("UTC"));
    }

    public ZonedDateTime getEndDate() {
        LocalDateTime local = getEndControl().getLocalDateTime();
        return ZonedDateTime.of(local, ZoneId.of("UTC"));
    }

    public void setStartDate(LocalDateTime local) {
        getStartControl().setLocalDateTime(local);
    }

    public void setEndDate(LocalDateTime local) {
        getEndControl().setLocalDateTime(local);
    }

    public void setOnScan(Runnable runnable) {
        scanButton.setOnAction(eh -> runnable.run());
    }

    @Override
    public Optional<IConstraint> getConstraint() {
        return Optional.of(new DateBoundsConstraint(getValueName(),
                new Date(getStartDate().toInstant().toEpochMilli()),
                new Date(getEndDate().toInstant().toEpochMilli())));
    }
}
