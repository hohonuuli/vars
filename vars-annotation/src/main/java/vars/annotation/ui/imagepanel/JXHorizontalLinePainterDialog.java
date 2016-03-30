package vars.annotation.ui.imagepanel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.*;

import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.DynamicList;
import vars.annotation.ui.StateLookup;
import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.border.TitledBorder;

import org.mbari.swing.ListListModel;

/**
 * @author Brian Schlining
 * @since 2014-11-19T13:30:00
 */
public class JXHorizontalLinePainterDialog extends StandardDialog {
	
	
	public enum Status { OK, Cancel }
	
	private Status status;
	private JScrollPane scrollPane;
	private DynamicList list;
	private final JXHorizontalLinePainter painter;
	
	
	public JXHorizontalLinePainterDialog(JXHorizontalLinePainter painter) {
        this(StateLookup.getAnnotationFrame(), painter);
    }

    public JXHorizontalLinePainterDialog(Frame parent, JXHorizontalLinePainter painter) {
        super(parent);

        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        status = Status.Cancel;
		setTitle("Add Horizontal Lines (0 < percent < 1)");
		this.painter = painter;
    }
    
    private void initialize() throws Exception {
    	getContentPane().add(getScrollPane(), BorderLayout.CENTER);
		getOkayButton().addActionListener(new ActionAdapter() {
			@Override
			public void doAction() {
				final ListModel<String> model = getList().getModel();
				Collection<Double> distances = new ArrayList<Double>();
				for (int i = 0; i < model.getSize(); i++) {
					String s = model.getElementAt(i);
					try {
						double d = Double.parseDouble(s);
						distances.add(d);
					}
					catch (Exception e) {
						// Do nothing, the string was not a double
					}
				}
				painter.setDistances(distances);
				cleanupList();
				setVisible(false);
			}
		});

		getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanupList();
				setVisible(false);
			}
		});
    }

	private void cleanupList() {
		ArrayList<Double> distances = new ArrayList<Double>(painter.getDistances());
		Collections.sort(distances);
		final ListListModel model = getList().getContent();
		model.clear();
		for (Double d : distances) {
			model.add(d.toString());
		}
	}

	public Status getStatus() {
		return status;
	}
	
	protected DynamicList getList() {
		if (list == null) {
			list = new DynamicList("0.");
			list.setBorder(new TitledBorder("Enter to add, backspace to remove"));
		}
		return list;
		
	}
	
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getList());
			
			scrollPane.setViewportBorder(new TitledBorder(null, "Horizontal Lines as percent", 
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return scrollPane;
	}



}
