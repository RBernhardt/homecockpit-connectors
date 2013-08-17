package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

public class HeadingConverter implements ValueConverter<ByteArray, Double> {

	@Override
	public Double toInput(ByteArray data) {
		return FSUIPCUtil.toHeading(data.toInt());
	}

	@Override
	public ByteArray toOutput(Double data) {
        return ByteArray.create(FSUIPCUtil.toFSUIPCHeading(data), 4);
	}

}