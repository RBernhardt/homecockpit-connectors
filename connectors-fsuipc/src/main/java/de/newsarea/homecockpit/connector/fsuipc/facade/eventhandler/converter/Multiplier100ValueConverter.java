package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;

public class Multiplier100ValueConverter implements ValueConverter<ByteArray, Number> {

	@Override
	public Number toInput(ByteArray data) {
		Short sdata = data.toShort();
        return sdata * 100;
	}

	@Override
	public ByteArray toOutput(Number data) {
        return ByteArray.create(Integer.parseInt(data.toString()) / 100, 8);
	}

}
