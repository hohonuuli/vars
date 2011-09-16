package vars.annotation.ui.imagepanel;

import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

import java.awt.Frame;

/**
 * @author Brian Schlining
 * @since 2011-09-13
 */
public class MeasurementCommentDialog extends AddCommentAssociationDialog {

    public MeasurementCommentDialog(Frame parent) {
        super(parent);
        setTitle("VARS - Enter a Comment About this Measurement");
    }
}