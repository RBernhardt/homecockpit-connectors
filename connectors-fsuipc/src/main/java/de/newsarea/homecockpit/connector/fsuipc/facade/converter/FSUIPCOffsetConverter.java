package de.newsarea.homecockpit.connector.fsuipc.facade.converter;

import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSUIPCOffsetConverter implements Converter {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCOffsetConverter.class);

    @Override
    public Object convert(Class type, Object value) {
        if(type == FSUIPCOffset.class && value instanceof String) {
            int offset = Integer.parseInt(value.toString().replaceAll("0x", ""), 16);
            return new FSUIPCOffset(offset);
        }
        throw new ConversionException("can't convert " + value.toString());
    }

}
