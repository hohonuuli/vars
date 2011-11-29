/*
 * @(#)CommandQueue.java   2011.11.15 at 04:45:51 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.commandqueue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The command queue processes commands in the order received. Commands are send via
 * EventBus as CommandEvents. There are 2 types of CommandEvents, DoCommmandEvent and
 * UndoCommandEvent. For all practical purposes components should use only the
 * {@link DoCommandEvent}; the {@link UndoCommandEvent} is mainly for internal use only.
 * Other events that the command queue listens for are:
 * <ul>
 *     <li>{@link ClearCommandQueueEvent}</li>
 *     <li>{@link RedoEvent}</li>
 *     <li>{@link UndoEvent}</li>
 * </ul>
 * @author Brian Schlining
 * @since 2011-09-21
 */
public class CommandQueue {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final int maxUndos = 25;
    private final Queue<CommandEvent> pendingQueue = new ConcurrentLinkedQueue<CommandEvent>();
    private final Deque<CommandEvent> undos = new LinkedBlockingDeque<CommandEvent>(maxUndos);
    private final Deque<CommandEvent> redos = new LinkedBlockingDeque<CommandEvent>(maxUndos);
    private final Thread thread;
    private final ToolBelt toolBelt;

    private final Runnable runnable = new Runnable() {

        private volatile boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                CommandEvent commandEvent = pendingQueue.poll();
                if (commandEvent != null) {
                    Command command = commandEvent.getCommand();
                    try {
                        log.debug("Executing Command: " + commandEvent.getDoOrUndo() + " - " +
                                command.getDescription());

                        // Execute the command (can be DO or UNDO operation)
                        Deque<CommandEvent> inverseCommandList = null;
                        switch (commandEvent.getDoOrUndo()) {
                        case DO: {
                            command.apply(toolBelt);
                            inverseCommandList = undos;
                            break;
                        }
                        case UNDO: {
                            command.unapply(toolBelt);
                            inverseCommandList = redos;
                            break;
                        }
                        }

                        // Put the command
                        int size = inverseCommandList.size();
                        if (size >= maxUndos) {
                            inverseCommandList.pollFirst();
                        }
                        inverseCommandList.offerLast(commandEvent);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    }
                }
            }
        }

        void kill() {
            isRunning = false;
        }
    };


    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public CommandQueue(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        AnnotationProcessor.process(this);
        thread = new Thread(runnable, getClass().getName());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ClearCommandQueueEvent.class)
    public void clear(ClearCommandQueueEvent event) {
        undos.clear();
        redos.clear();
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = CommandEvent.class)
    public void queueCommand(CommandEvent event) {
        pendingQueue.offer(event);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = RedoEvent.class)
    public void redo(RedoEvent event) {
        if (redos.size() > 0) {
            CommandEvent commandEvent = redos.removeLast();
            CommandEvent newCommandEvent = new CommandEvent(commandEvent.getCommand(), CommandEvent.DoOrUndo.DO);
            queueCommand(newCommandEvent);
        }
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = UndoEvent.class)
    public void undo(UndoEvent event) {
        if (undos.size() > 0) {
            CommandEvent commandEvent = undos.removeLast();
            CommandEvent newCommandEvent = new CommandEvent(commandEvent.getCommand(), CommandEvent.DoOrUndo.UNDO);
            queueCommand(newCommandEvent);
        }
    }
}
