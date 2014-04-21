package de.newsarea.homecockpit.connector.fsuipc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FSUIPCHttpConnector implements FSUIPCConnector {

    private static Logger log = LoggerFactory.getLogger(FSUIPCHttpConnector.class);

    private HttpClient httpClient;
    private HttpHost httpHost;
    private int socketPort;
    private Socket socket;

    private EventListenerSupport<ValueChangedEventListener> eventListeners;

    private boolean closed = true;
    private ExecutorService executorService;
    private Gson gson;
    private Map<String, Integer> timeOfBlockingList;

    public Map<String, Integer> getTimeOfBlockingList() {
        return timeOfBlockingList;
    }

    public void setTimeOfBlockingList(Map<String, Integer> timeOfBlockingList) {
        if(timeOfBlockingList == null) {
            throw new IllegalArgumentException("timeOfBlockingList must be not null");
        }
        this.timeOfBlockingList = timeOfBlockingList;
    }

    public FSUIPCHttpConnector(String host, int httpPort, int socketPort) {
        this.httpClient = HttpClientBuilder.create().build();
        this.httpHost = new HttpHost(host, httpPort);
        this.socketPort = socketPort;
        // ~
        this.eventListeners = EventListenerSupport.create(ValueChangedEventListener.class);
        this.gson = new Gson();
        this.timeOfBlockingList = new HashMap<>();
    }

    @Override
    public boolean isConnectionEstablished() {
        if(closed) {
            return false;
        }
        //
        try {
            URIBuilder uriBuilder = new URIBuilder("/status");
            log.info("excute - GET {}", uriBuilder.toString());
            HttpResponse response = httpClient.execute(httpHost, new HttpGet(uriBuilder.build()));
            String responseAsString = responseToString(response);
            int status = response.getStatusLine().getStatusCode();
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder msg = new StringBuilder();
                msg.append(status);
                if(StringUtils.isNotEmpty(responseAsString)) {
                    msg.append(" - ").append(responseAsString);
                }
                throw new IOException(msg.toString());
            }
            // @TODO HACK es sollte nur der Wert überprüft werden
            return responseAsString.equalsIgnoreCase("status=OPEN");
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void monitor(OffsetIdent offsetIdent) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder("/monitor/");
            StringBuilder data = new StringBuilder();
            data.append("offset=").append(offsetToHexString(offsetIdent.getOffset()));
            data.append("&");
            data.append("size=").append(offsetIdent.getSize());
            log.info("excute - POST {} with {}", uriBuilder.toString(), data);
            // create method with entity
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            StringEntity dataEntity = new StringEntity(data.toString());
            dataEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(dataEntity);
            // ~
            HttpResponse response = httpClient.execute(httpHost, httpPost);
            String responseAsString = responseToString(response);
            int status = response.getStatusLine().getStatusCode();
            if(status != 200) {
                StringBuilder msg = new StringBuilder();
                msg.append(status);
                if(StringUtils.isNotEmpty(responseAsString)) {
                    msg.append(" - ").append(responseAsString);
                }
                throw new IOException(msg.toString());
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void write(OffsetItem offsetItem) throws IOException {
        try {
            String offsetHexString = offsetToHexString(offsetItem.getOffset());
            // determine timeOfBlocking
            int timeOfBlocking = 0;
            if(timeOfBlockingList.containsKey(offsetHexString)) {
                timeOfBlocking = timeOfBlockingList.get(offsetHexString);
            }
            // ~
            URIBuilder uriBuilder = new URIBuilder("/offsets/" + offsetHexString);
            StringBuilder data = new StringBuilder();
            data.append("data=").append(offsetItem.getValue().toHexString());
            data.append("&");
            data.append("timeOfBlocking=").append(timeOfBlocking);
            log.info("excute - PUT {} with {}", uriBuilder.toString(), data);
            // create method with entity
            HttpPut httpPut = new HttpPut(uriBuilder.build());
            StringEntity dataEntity = new StringEntity(data.toString());
            dataEntity.setContentType("application/x-www-form-urlencoded");
            httpPut.setEntity(dataEntity);
            // ~
            HttpResponse response = httpClient.execute(httpHost, httpPut);
            String responseAsString = responseToString(response);
            int status = response.getStatusLine().getStatusCode();
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder msg = new StringBuilder();
                msg.append(status);
                if(StringUtils.isNotEmpty(responseAsString)) {
                    msg.append(" - ").append(responseAsString);
                }
                throw new IOException(msg.toString());
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public OffsetItem read(OffsetIdent offsetIdent) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder("/offsets/" + offsetToHexString(offsetIdent.getOffset()));
            uriBuilder.addParameter("size", String.valueOf(offsetIdent.getSize()));
            log.info("excute - GET {}", uriBuilder.toString());
            HttpResponse response = httpClient.execute(httpHost, new HttpGet(uriBuilder.build()));
            String responseAsString = responseToString(response);
            int status = response.getStatusLine().getStatusCode();
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder msg = new StringBuilder();
                msg.append(status);
                if(StringUtils.isNotEmpty(responseAsString)) {
                    msg.append(" - ").append(responseAsString);
                }
                throw new IOException(msg.toString());
            }
            // ~
            ByteArray data = ByteArray.create(responseAsString);
            return new OffsetItem(offsetIdent.getOffset(), offsetIdent.getSize(), data);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void open() throws ConnectException {
        try {
            closed = false;
            // ~
            socket = new Socket(httpHost.getHostName(), socketPort);
        } catch (IOException e) {
            throw new ConnectException(e.getMessage());
        }
        // ~
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handleSocket(socket);
            }
        });
    }

    private void handleSocket(Socket socket) {
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                if(closed) { break; }
                handleMessage(line);
            }
        } catch (SocketException se) {
            if(!closed) {
                log.error(se.getMessage(), se);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleMessage(String jsonString) {
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        for(int i=0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String offsetString = jsonObject.get("offset").getAsString();
            offsetString = offsetString.replaceAll("0x", "");
            int offset = Integer.parseInt(offsetString, 16);
            int size = Integer.parseInt(jsonObject.get("size").getAsString());
            ByteArray data = ByteArray.create(jsonObject.get("data").getAsString());
            // ~
            eventListeners.fire().valueChanged(new FSUIPCConnectorEvent(offset, size, data));
        }
    }

    @Override
    public void addEventListener(ValueChangedEventListener<FSUIPCConnectorEvent> eventListener) {
        eventListeners.addListener(eventListener);
    }

    @Override
    public void close() {
        closed = true;
        // close socket
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        // shutdown executor service
        if(executorService != null) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FSUIPCHttpConnector)) return false;

        FSUIPCHttpConnector that = (FSUIPCHttpConnector) o;

        if (closed != that.closed) return false;
        if (socketPort != that.socketPort) return false;
        if (eventListeners != null ? !eventListeners.equals(that.eventListeners) : that.eventListeners != null)
            return false;
        if (executorService != null ? !executorService.equals(that.executorService) : that.executorService != null)
            return false;
        if (gson != null ? !gson.equals(that.gson) : that.gson != null) return false;
        if (httpClient != null ? !httpClient.equals(that.httpClient) : that.httpClient != null) return false;
        if (httpHost != null ? !httpHost.equals(that.httpHost) : that.httpHost != null) return false;
        if (socket != null ? !socket.equals(that.socket) : that.socket != null) return false;
        if (timeOfBlockingList != null ? !timeOfBlockingList.equals(that.timeOfBlockingList) : that.timeOfBlockingList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = httpClient != null ? httpClient.hashCode() : 0;
        result = 31 * result + (httpHost != null ? httpHost.hashCode() : 0);
        result = 31 * result + socketPort;
        result = 31 * result + (socket != null ? socket.hashCode() : 0);
        result = 31 * result + (eventListeners != null ? eventListeners.hashCode() : 0);
        result = 31 * result + (closed ? 1 : 0);
        result = 31 * result + (executorService != null ? executorService.hashCode() : 0);
        result = 31 * result + (gson != null ? gson.hashCode() : 0);
        result = 31 * result + (timeOfBlockingList != null ? timeOfBlockingList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FSUIPCHttpConnector{" +
                "httpClient=" + httpClient +
                ", httpHost=" + httpHost +
                ", socketPort=" + socketPort +
                ", socket=" + socket +
                ", eventListeners=" + eventListeners +
                ", closed=" + closed +
                ", executorService=" + executorService +
                ", gson=" + gson +
                ", timeOfBlockingList=" + timeOfBlockingList +
                "}";
    }

    /* HELPER */

    private String responseToString(HttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    private static String offsetToHexString(int offset) {
        String hexString = ByteArray.create(String.valueOf(offset), 2).toHexString();
        return hexString.replaceAll("0x", "");
    }

}
