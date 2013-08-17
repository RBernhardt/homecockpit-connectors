package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeverValueConverter implements ValueConverter<ByteArray, Number> {
	
	private static Logger log = LoggerFactory.getLogger(LeverValueConverter.class);

	@Override
	public Number toInput(ByteArray data) {
		short sValue = data.toShort();
        if (sValue < 0) { return 0; }
        return (byte)Math.round(sValue / 16384D * 100D);
	}

	@Override
	public ByteArray toOutput(Number nbrValue) {
		byte data = Byte.parseByte(nbrValue.toString());
        if (data < 0 || data > 100) { log.debug("invalid argument - {}", data); }
        return ByteArray.create(Math.round(data / 100D * 16384D), 4);
	}

}
