package de.newsarea.homecockpit.connector.event;

import de.newsarea.homecockpit.connector.Connector;

public interface ConnectorEventListener {
	
	public enum State {		
		OPEN, CONNECTED, CLOSED		
	}
	
	void stateChanged(Connector connector, State state);

}
