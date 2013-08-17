package de.newsarea.homecockpit.connector.util;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.event.ConnectorEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class ConnectorConnectionHelper {
	
	private static Logger log = LoggerFactory.getLogger(ConnectorConnectionHelper.class);
	
	private Connector connector;
	private List<ConnectorEventListener> connectorEventListeners = new ArrayList<>();
	
	private ConnectionWorkerThread connectionWorkerThread;

	public ConnectorConnectionHelper(Connector connector) {
		this.connector = connector;
		this.connectionWorkerThread = new ConnectionWorkerThread(connector);
		this.connectionWorkerThread.addEventListener(new ConnectorEventListener() {			
			@Override
			public void stateChanged(Connector connector, State state) {
				fireEvent(state);
			}
		});
	}
	
	public void open() throws ConnectException {
		fireEvent(ConnectorEventListener.State.OPEN);
		connector.open();
		fireEvent(ConnectorEventListener.State.CONNECTED);
	}

	public void open(int retryTimeout) {
		connectionWorkerThread.setRetryTimeout(retryTimeout);
		connectionWorkerThread.start();
	}
	
	public void close() {
		connectionWorkerThread.exit();
		try {
			connectionWorkerThread.join();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		connector.close();
		fireEvent(ConnectorEventListener.State.CLOSED);
	}
	
	/* EVENT */
	
	public void addEventListener(ConnectorEventListener connectorEventListener) {
		connectorEventListeners.add(connectorEventListener);
	}
	
	private void fireEvent(ConnectorEventListener.State state) {
		for(ConnectorEventListener connectorEventListener : connectorEventListeners) {
			connectorEventListener.stateChanged(connector, state);
		}
	}
	
	/* THREAD */
	
	private static class ConnectionWorkerThread extends Thread {
		
		private List<ConnectorEventListener> connectorEventListeners = new ArrayList<ConnectorEventListener>();
		
		private Connector connector;
		private boolean exit = false;
		
		private int retryTimeout = 0;
		
		public void setRetryTimeout(int timeout) {
			this.retryTimeout = timeout;
		}
		
		public ConnectionWorkerThread(Connector connector) {
			this.connector = connector;
		}
		
		public void run() {
            exit = false;
            while(!exit) {
                try {
					fireEvent(ConnectorEventListener.State.OPEN);
					connector.open();
					fireEvent(ConnectorEventListener.State.CONNECTED);
					return;
				} catch(ConnectException e) {
				} catch(Exception e) {
					log.error(e.getMessage(), e);
				}
				//
				try {
					Thread.sleep(retryTimeout);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
		public void exit() {
			exit = true;
		}
		
		public void addEventListener(ConnectorEventListener eventListener) {
			connectorEventListeners.add(eventListener);
		}
		
		public void fireEvent(ConnectorEventListener.State state) {
			for(ConnectorEventListener connectorEventListener : connectorEventListeners) {
				connectorEventListener.stateChanged(connector, state);
			}
		}
		
	}

}
