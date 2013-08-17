package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

public interface ValueConverter<I, O> {

	O toInput(I data);
	I toOutput(O data);
	
}
