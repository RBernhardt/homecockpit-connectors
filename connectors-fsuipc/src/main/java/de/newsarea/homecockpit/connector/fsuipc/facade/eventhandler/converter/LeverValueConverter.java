package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeverValueConverter implements ValueConverter<Long, Long> {
	
	private static Logger log = LoggerFactory.getLogger(LeverValueConverter.class);

	@Override
	public Long toInput(Long data) {
        if (data < 0) { return 0L; }
        return Math.round(data / 16384D * 100D);
	}

	@Override
	public Long toOutput(Long data) {
        if (data < 0 || data > 100) { log.debug("invalid argument - {}", data); }
        return Math.round(data / 100D * 16384D);
	}

}
