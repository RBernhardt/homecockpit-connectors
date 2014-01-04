package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import com.google.common.primitives.Ints;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

public class HeadingConverter implements ValueConverter<Long, Double> {

	@Override
	public Double toInput(Long data) {
		return FSUIPCUtil.toHeading(Ints.checkedCast(data));
	}

	@Override
	public Long toOutput(Double data) {
        return Long.valueOf(FSUIPCUtil.toFSUIPCHeading(data));
	}

}