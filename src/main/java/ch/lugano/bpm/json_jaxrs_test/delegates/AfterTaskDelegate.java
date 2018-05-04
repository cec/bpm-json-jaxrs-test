package ch.lugano.bpm.json_jaxrs_test.delegates;

import ch.lugano.bpm.json_jaxrs_test.shared.MyDTO;
import ch.lugano.bpm.json_jaxrs_test.shared.RequestLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;

import javax.inject.Named;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS;
import static org.camunda.spin.Spin.JSON;

@Named("After")
public class AfterTaskDelegate extends AbstractDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        super.execute(execution);
        execution.getVariables().forEach((k, v) -> log("VAR: " + k + " = " + v));
        log("Making a POST to a non-existing API just to log the serialized request");

        MyDTO dto = JSON(execution.getVariable("dto")).mapTo(MyDTO.class);
        dto.setTimeStamp(LocalDateTime.now());

        try {
            baseTarget().request().accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(dto, MediaType.APPLICATION_JSON));
        } catch (ProcessingException e) {
            // expected as the target API does not exist
        }
    }

    private WebTarget baseTarget() {
        ClientBuilder builder = ClientBuilder.newBuilder()
                .register(new RequestLogger())
                .register(buildJsonProvider());
//		builder.sslContext(new SSLContext()); // enable SSL
        return builder.build().target("http://localhost");
    }

    /**
     * Note that we are importing com.fasterxml.jackson.databind.ObjectMapper and not the ones in SpinJar
     */
    private JacksonJsonProvider buildJsonProvider() { // IMPORTANT
        return new JacksonJsonProvider(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule()) //per format LocalDateTime nelle Entities
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                        .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"))
                , DEFAULT_ANNOTATIONS);
    }

}
