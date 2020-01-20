package socket.handler.jetty;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.function.Function;

public class HandlerPipeLineBuilder {

    public Function<RequestContext, RequestContext> pipeline() {
        return pipeline;
    }

    private Function<RequestContext, RequestContext> markComplete = (context) -> {
        context.baseRequest.setHandled(true);
        return context;
    };

    private Function<RequestContext, RequestContext> process = (context) -> {
        var jsonParser = new Gson();

        var textStream = toReader(context.request);

        Object parsedValue = jsonParser.fromJson(textStream, context.processor.inputType());

        context.output = context.processor.process(parsedValue);

        return context;
    };

    private Reader toReader(HttpServletRequest request) {
        try {
            return new InputStreamReader(request.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PrintWriter toWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Function<RequestContext, RequestContext> prepareForWrite = (context) -> {
        context.response.setContentType("application/json; charset=utf-8");
        context.response.setStatus(HttpServletResponse.SC_OK);
        return context;
    };

    private Function<RequestContext, RequestContext> writeResponse = (context) -> {
        PrintWriter out = toWriter(context.response);
        out.println(new Gson().toJson(context.output));
        return context;
    };

    private final Function<RequestContext, RequestContext> pipeline = process
            .andThen(prepareForWrite).andThen(writeResponse).andThen(markComplete);

    static class RequestContext {
        final Request baseRequest;
        final HttpServletRequest request;
        final HttpServletResponse response;
        final RequestProcessor processor;
        Object output;

        public RequestContext(HttpServletRequest request, HttpServletResponse response, RequestProcessor processor,
                              Request baseRequest) {
            this.request = request;
            this.response = response;
            this.processor = processor;
            this.baseRequest = baseRequest;
        }

    }
}
