package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.event.InboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

class FSUIPCSwingUI extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCSwingUI.class);

    private final ConnectorFacade connectorFacade;
    private JLabel lLatLabel;
    private JLabel lLngLabel;
    private JLabel lAltLabel;

    private boolean isPause = false;

    public FSUIPCSwingUI(ConnectorFacade connectorFacade) {
        this.connectorFacade = connectorFacade;
    }

    public void initialize() {
        setTitle("FSUIPC Example");
        setSize(300, 160);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // add latitude
        add(createTitleLabel("Latitude:", 30));
        lLatLabel = createValueLabel("0.0", 30);
        add(lLatLabel);
        // add longitude
        add(createTitleLabel("Longitude:", 10));
        lLngLabel = createValueLabel("0.0", 10);
        add(lLngLabel);
        // add altitude
        add(createTitleLabel("Altitude:", 50));
        lAltLabel = createValueLabel("0", 50);
        add(lAltLabel);
        // add pause hint
        add(createTitleLabel("PRESS [P] TO PAUSE", 100));
        // ~
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 'P') {
                    try {
                        togglePause();
                    } catch(Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
        // ~
        initializeFSUIPCInterface();
    }

    private void initializeFSUIPCInterface() {
        connectorFacade.addEventListener(new OutboundEventListener() {
            @Override
            public void outboundEvent(OutboundEvent outboundEvent) {
                handleOutboundEvent(outboundEvent);
            }
        });
    }

    /***
     * Offset: 0x0262; Size: 2;
     */
    private void togglePause() throws IOException {
        if(isPause) {
            connectorFacade.postEvent(new InboundEvent("MAIN", "PAUSE", "OFF", null));
            isPause = false;
        } else {
            connectorFacade.postEvent(new InboundEvent("MAIN", "PAUSE", "ON", null));
            isPause = true;
        }
    }

    private void handleOutboundEvent(OutboundEvent outboundEvent) {
        if(outboundEvent.getComponent().equals("LATITUDE")) {
            lLatLabel.setText(outboundEvent.getValue().toString());
        } else if(outboundEvent.getComponent().equals("LONGITUDE")) {
            lLngLabel.setText(outboundEvent.getValue().toString());
        } else if(outboundEvent.getComponent().equals("ALTITUDE")) {
            lAltLabel.setText(outboundEvent.getValue().toString());
        }
    }

    private JLabel createTitleLabel(String title, int y) {
        JLabel label = new JLabel(title);
        label.setLocation(10, y);
        label.setSize(250, 20);
        return label;
    }

    private JLabel createValueLabel(String defaultValue, int y) {
        JLabel label = new JLabel(defaultValue);
        label.setLocation(100, y);
        label.setSize(200, 20);
        return label;
    }

}
