/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vars.shared.ui;

import org.bushe.swing.event.EventTopicSubscriber;

/**
 *
 * @author brian
 */
public class ExitTopicSubscriber implements EventTopicSubscriber {

    public static final String TOPIC_EXIT = "System Exit Topic";

    public void onEvent(String topic, Object data) {
        System.exit(0);
    }
};
