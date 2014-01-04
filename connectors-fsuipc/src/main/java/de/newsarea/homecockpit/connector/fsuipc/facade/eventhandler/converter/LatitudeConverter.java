package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

public class LatitudeConverter implements ValueConverter<Long, Double> {

	@Override
	public Double toInput(Long data) {
		return FSUIPCUtil.toLatitude(data);
	}

	@Override
	public Long toOutput(Double data) {
        return FSUIPCUtil.toFSUIPCLatitude(data);
	}

}
