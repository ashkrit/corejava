package socket.handler.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import socket.handler.jetty.HandlerPipeLineBuilder.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class GenericRequestHandler extends AbstractHandler {

    private final Map<String, RequestProcessor> contextToRequest = new HashMap<>();
    private final HandlerPipeLineBuilder pipelineBuilder = new HandlerPipeLineBuilder();

    public void map(String context, RequestProcessor processor) {
        contextToRequest.put(context, processor);
    }

    @Override
    public void handle(String contextPath, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response) {

        var processor = contextToRequest.get(contextPath);
        RequestContext context = new RequestContext(request, response, processor, baseRequest);
        pipelineBuilder.pipeline().apply(context);

    }
}
