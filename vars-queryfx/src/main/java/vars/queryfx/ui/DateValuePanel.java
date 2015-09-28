package vars.queryfx.ui;

import com.guigarage.sdk.util.MaterialDesignButton;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import jfxtras.scene.control.LocalDateTimePicker;
import vars.queryfx.Lookup;
import vars.queryfx.ui.db.DateBoundsConstraint;
import vars.queryfx.ui.db.IConstraint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-28T11:28:00
 */
public class DateValuePanel extends AbstractValuePanel {

    private LocalDateTimePicker startPicker;
    private LocalDateTimePicker endPicker;
    private ChangeListener<LocalDateTime> changeListener = (obs, oldVal, newVal) ->
        getConstrainCheckBox().setSelected(true);
    private Button scanButton = new MaterialDesignButton("Scan");


    public DateValuePanel(String valueName) {
        super(valueName);
        getChildren().addAll(getStartPicker(), getEndPicker());
    }

    private LocalDateTimePicker getEndPicker() {
        if (endPicker == null) {
            endPicker = new LocalDateTimePicker(LocalDateTime.now());
            //endPicker.localDateTimeProperty().addListener(changeListener);
        }
        return endPicker;
    }

    private LocalDateTimePicker getStartPicker() {
        if (startPicker == null) {
            startPicker = new LocalDateTimePicker(Lookup.getAnnotationStartDate().toLocalDateTime());
            //startPicker.localDateTimeProperty().addListener(changeListener);
        }
        return startPicker;
    }

    public ZonedDateTime getStartDate() {
        LocalDateTime local = getStartPicker().getLocalDateTime();
        return ZonedDateTime.of(local, ZoneId.of("UTC"));
    }

    public ZonedDateTime getEndDate() {
        LocalDateTime local = getEndPicker().getLocalDateTime();
        return ZonedDateTime.of(local, ZoneId.of("UTC"));
    }

    public void setStartDate(LocalDateTime local) {
        getStartPicker().setLocalDateTime(local);
    }

    public void setEndDate(LocalDateTime local) {
        getEndPicker().setLocalDateTime(local);
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
