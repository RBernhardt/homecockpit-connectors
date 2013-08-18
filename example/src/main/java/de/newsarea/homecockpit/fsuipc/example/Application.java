package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.DefaultConnectorFacade;
import de.newsarea.homecockpit.connector.facade.registration.XmlEventHandlerRegistrationService;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCGeneralConnector;
import de.newsarea.homecockpit.connector.net.NetGeneralConnector;
import de.newsarea.homecockpit.connector.util.ConnectorConnectionHelper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;

public class Application {

    private Connector connector;
    private ConnectorConnectionHelper connectorConnectionHelper;
    private FSUIPCSwingUI fsuipcSwingUI;

    public static void main(String[] args) throws InterruptedException, ConnectException {
        FSUIPCConnector fsuipcConnector = new FSUIPCGeneralConnector(new NetGeneralConnector("drevil", 4020));
        // ~
        Application app = new Application();
        app.run(fsuipcConnector);
    }

    public void run(Connector inputConnector) throws ConnectException {
        connector = inputConnector;
        // ~
        connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.open();
        // ~
        ConnectorFacade connectorFacade = new DefaultConnectorFacade();
        // ~
        XmlEventHandlerRegistrationService xmlEventHandlerRegistrationService = new XmlEventHandlerRegistrationService(connectorFacade);
        xmlEventHandlerRegistrationService.registerEventHandler(connector, "/de/newsarea/homecockpit/connector/fsuipc/general.xml");
        // ~
        fsuipcSwingUI = new FSUIPCSwingUI(connectorFacade);
        fsuipcSwingUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connector.close();
            }
        });
        fsuipcSwingUI.initialize();
        // show frame
        fsuipcSwingUI.setVisible(true);
    }

    public void shutdown() {
        fsuipcSwingUI.setVisible(false);
        connectorConnectionHelper.close();
    }

}
