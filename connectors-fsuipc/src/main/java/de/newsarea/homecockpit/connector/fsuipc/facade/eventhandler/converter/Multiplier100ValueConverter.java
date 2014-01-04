package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

public class Multiplier100ValueConverter implements ValueConverter<Integer, Long> {

	@Override
	public Long toInput(Integer data) {
        return Long.valueOf(data * 100);
	}

	@Override
	public Integer toOutput(Long data) {
        return data.intValue() / 100;
	}

}
