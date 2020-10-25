import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;


public class HttpUtils {

    private static CloseableHttpClient httpClient;

    private static final int SOCKE_TTIMEOUT = 2000;
    private static final int CONNECT_TIMEOUT = 2000;
    private static final int REQUEST_TIMEOUT = 2000;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(builder.build()))
                    .build();

            PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolConnManager.setMaxTotal(200);
            poolConnManager.setDefaultMaxPerRoute(100);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                    .setSocketTimeout(SOCKE_TTIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(poolConnManager)
                    .setDefaultRequestConfig(requestConfig)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String url, Map<String, Object> params, Map<String, String> headers){
        StringBuilder requestUrl = new StringBuilder(url);
        if(params != null){
            requestUrl.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestUrl.append(entry.getKey()).append("=").append(entry.getValue());
                requestUrl.append("&");
            }
        }

        HttpRequestBase http = new HttpGet(requestUrl.toString());
        buildHeaders(headers, http);

        String result = null;
        try (CloseableHttpResponse response = httpClient.execute(http)){
            HttpEntity entity = response.getEntity();
            if(entity != null){
                result = EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void buildHeaders(Map<String, String> headers, HttpRequestBase http) {
        if(headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
