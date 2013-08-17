package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;


import de.newsarea.homecockpit.fsuipc.domain.ByteArray;

public class Divider128ValueConverter implements ValueConverter<ByteArray, Integer> {

	@Override
	public Integer toInput(ByteArray data) {
		Integer nbr = (Integer) data.toNumber(4);
		return (int)(nbr / 128D);
	}

	@Override
	public ByteArray toOutput(Integer data) {
        return ByteArray.create(data * 128D, 4);
	}

}
