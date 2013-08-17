package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.DefaultConnectorFacade;
import de.newsarea.homecockpit.connector.facade.registration.XmlEventHandlerRegistrationService;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCNetConnector;
import de.newsarea.homecockpit.connector.net.NetGeneralConnector;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;

public class Application {

    public static void main(String[] args) throws InterruptedException, ConnectException {
        FSUIPCConnector fsuipcConnector = new FSUIPCNetConnector(new NetGeneralConnector("drevil", 4020));
        // ~
        Application app = new Application();
        app.run(fsuipcConnector);
    }

    public void run(final Connector connector) throws ConnectException {
        connector.open();
        // ~
        ConnectorFacade connectorFacade = new DefaultConnectorFacade();
        // ~
        XmlEventHandlerRegistrationService xmlEventHandlerRegistrationService = new XmlEventHandlerRegistrationService(connectorFacade);
        xmlEventHandlerRegistrationService.registerEventHandler(connector, "/de/newsarea/homecockpit/connector/fsuipc/general.xml");
        // ~
        FSUIPCSwingUI fsuipSwingUI = new FSUIPCSwingUI(connectorFacade);
        fsuipSwingUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connector.close();
            }
        });
        fsuipSwingUI.initialize();
        // show frame
        fsuipSwingUI.setVisible(true);
    }

}
