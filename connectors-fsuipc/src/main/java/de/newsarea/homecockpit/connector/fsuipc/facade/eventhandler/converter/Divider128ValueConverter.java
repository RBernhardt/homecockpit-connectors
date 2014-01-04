package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;


import com.google.common.primitives.Ints;

public class Divider128ValueConverter implements ValueConverter<Long, Integer> {

	@Override
	public Integer toInput(Long data) {
		return Ints.checkedCast(Math.round(data / 128D));
    }

	@Override
	public Long toOutput(Integer data) {
        return Math.round(data * 128D);
	}

}
