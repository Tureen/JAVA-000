package club.tulane.customgateway.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class HeadFilter implements Filter {

    @Override
    public void filter(FullHttpRequest fullRequest) {
        final HttpHeaders headers = fullRequest.headers();
        headers.add("nio", "tulane");
    }
}
