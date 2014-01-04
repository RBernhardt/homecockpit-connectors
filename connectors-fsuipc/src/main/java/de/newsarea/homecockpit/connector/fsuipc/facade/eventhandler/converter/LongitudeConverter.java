package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

public class LongitudeConverter implements ValueConverter<Long, Double> {

	@Override
	public Double toInput(Long data) {
		return FSUIPCUtil.toLongitude(data);
	}

	@Override
	public Long toOutput(Double data) {
        return FSUIPCUtil.toFSUIPCLongitude(data);
	}

}
