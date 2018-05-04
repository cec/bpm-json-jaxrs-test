package ch.lugano.bpm.json_jaxrs_test.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Logs everything about a request after being serialized
 */
public class RequestLogger implements ClientRequestFilter, WriterInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);
    private static final String ENTITY_STREAM_PROPERTY = "EntityLoggingFilter.entityStream";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final int maxEntitySize = 1024 * 8;


    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        URI uri = requestContext.getUri();
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        info(method);
        info(uri);
        headers.forEach((k, v) -> info(k + " : " + v));
        if (requestContext.hasEntity()) {
            final OutputStream stream = new LoggingStream(requestContext.getEntityStream());
            requestContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_STREAM_PROPERTY, stream);
        }

    }

    private void info(Object message) {
        LOGGER.info(message.toString());
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final LoggingStream stream = (LoggingStream) context.getProperty(ENTITY_STREAM_PROPERTY);
        context.proceed();
        if (stream != null) {
            log(stream.getStringBuilder(DEFAULT_CHARSET));
        }
    }


    private InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }


    private void log(StringBuilder sb) {
        info(sb.toString());
    }


    private class LoggingStream extends FilterOutputStream {
        private final StringBuilder sb = new StringBuilder();
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(OutputStream out) {
            super(out);
        }

        StringBuilder getStringBuilder(Charset charset) {
            // write entity to the builder
            final byte[] entity = baos.toByteArray();
            sb.append(new String(entity, 0, entity.length, charset));
            if (entity.length > maxEntitySize)
                sb.append("...more...");
            sb.append('\n');
            return sb;
        }

        @Override
        public void write(final int i) throws IOException {
            if (baos.size() <= maxEntitySize)
                baos.write(i);
            out.write(i);
        }
    }

}
