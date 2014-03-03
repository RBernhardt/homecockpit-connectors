package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

public class HeadingConverter implements ValueConverter<Long, Double> {

	@Override
	public Double toInput(Long data) {
		return FSUIPCUtil.toHeading(data);
	}

	@Override
	public Long toOutput(Double data) {
        return Long.valueOf(FSUIPCUtil.toFSUIPCHeading(data));
	}

}