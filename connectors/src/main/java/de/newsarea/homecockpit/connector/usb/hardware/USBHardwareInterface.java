package de.newsarea.homecockpit.connector.usb.hardware;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;
import de.newsarea.homecockpit.connector.event.ValueEventListener;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class USBHardwareInterface {

    private static Logger log = LoggerFactory.getLogger(USBHardwareInterface.class);

    private HIDManager hidManager;
    private HIDDevice hidDevice;
    private int vendorId;
    private int productId;
    private HIDDeviceReaderThread hidDeviceReaderThread;
    private List<ValueEventListener> valueEventListeners = new ArrayList<>();

    static {
        //System.loadLibrary("hidapi-jni");
    }

    public USBHardwareInterface(int vendorId, int productId) {
        this.vendorId = vendorId;
        this.productId = productId;
        //
        ClassPathLibraryLoader.loadNativeHIDLibrary();
    }

    public void open() throws ConnectException {
        if(hidManager != null) {
            throw new ConnectException("connection is open");
        }
        initHIDManager();
        initHIDDevice(vendorId, productId);
        initHIDDeviceReaderThread(hidDevice);
    }

    private void initHIDManager() throws ConnectException {
        try {
            hidManager = HIDManager.getInstance();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    private void initHIDDevice(int vendorId, int productId) throws ConnectException {
        try {
            hidDevice = HIDManager.getInstance().openById(vendorId, productId, null);
            log.info("Manufacturer: " + hidDevice.getManufacturerString());
            log.info("Product: " + hidDevice.getProductString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    private void initHIDDeviceReaderThread(HIDDevice hidDevice) {
        hidDeviceReaderThread = new HIDDeviceReaderThread(hidDevice);
        hidDeviceReaderThread.addEventListener(new ValueEventListener() {
            @Override
            public void valueReceived(String value) {
                fireValueReceivedEvent(value);
            }
        });
        hidDeviceReaderThread.start();
    }

    public void send(String value) throws IOException {
        try {
            byte[] data = Hex.decodeHex(value.toCharArray());
            hidDevice.write(data);
        } catch (DecoderException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void close() {
        if(hidDeviceReaderThread != null) {
            try {
                hidDeviceReaderThread.exit();
                hidDeviceReaderThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if(hidDevice != null) {
            try {
                hidDevice.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        if(hidManager != null) {
            hidManager.release();
        }
    }

    public void addEventListener(ValueEventListener valueEventListener) {
        valueEventListeners.add(valueEventListener);
    }

    protected void fireValueReceivedEvent(String value) {
        if(this.valueEventListeners.size() == 0) { return; }
        for(ValueEventListener valueEventListener : this.valueEventListeners) {
            valueEventListener.valueReceived(value);
        }
    }

    /* HELPER */

    private static final class HIDDeviceReaderThread extends Thread {

        private static final int BUFFERSIZE = 2048;

        private HIDDevice hidDevice;
        private List<ValueEventListener> valueEventListeners = new ArrayList<ValueEventListener>();
        private boolean exit = false;

        private HIDDeviceReaderThread(HIDDevice hidDevice) {
            this.hidDevice = hidDevice;
        }

        public void run() {
            log.info("starting usb reader thread");
            byte[] buffer = new byte[BUFFERSIZE];
            while(!exit) {
                int n = 0;
                try {
                    n = hidDevice.read(buffer);
                    String hexString = toHexString(buffer, n);
                    fireValueReceivedEvent(hexString);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        public void exit() {
            log.info("stopping usb reader thread");
            this.exit = true;
        }

        public void addEventListener(ValueEventListener valueEventListener) {
            valueEventListeners.add(valueEventListener);
        }

        private void fireValueReceivedEvent(String value) {
            if (this.valueEventListeners.size() == 0) {
                return;
            }
            for (ValueEventListener valueEventListener : this.valueEventListeners) {
                valueEventListener.valueReceived(value);
            }
        }

        /* HELPER */

        private static String toHexString(byte[] buffer, int n) {
            byte[] data = Arrays.copyOf(buffer, n);
            return new String(Hex.encodeHex(data));
        }

    }

    public static void printDeviceList() throws IOException {
        ClassPathLibraryLoader.loadNativeHIDLibrary();
        HIDManager hidManager = HIDManager.getInstance();
        try {
            HIDDeviceInfo[] devs = hidManager.listDevices();
            log.info("Devices:\n\n");
            for (int i = 0; i < devs.length; i++) {
                log.info("" + i + ".\t" + devs[i]);
                log.info("---------------------------------------------\n");
            }
            hidManager.release();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
