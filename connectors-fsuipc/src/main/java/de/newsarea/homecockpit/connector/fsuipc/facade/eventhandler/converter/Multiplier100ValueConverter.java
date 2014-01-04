package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

public class Multiplier100ValueConverter implements ValueConverter<Long, Long> {

	@Override
	public Long toInput(Long data) {
        return data * 100;
	}

	@Override
	public Long toOutput(Long data) {
        return data / 100;
	}

}
