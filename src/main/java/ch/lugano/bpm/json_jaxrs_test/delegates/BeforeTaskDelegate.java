package ch.lugano.bpm.json_jaxrs_test.delegates;

import ch.lugano.bpm.json_jaxrs_test.shared.MyDTO;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;

import javax.inject.Named;

import static org.camunda.spin.Spin.JSON;

@Named("Before")
public class BeforeTaskDelegate extends AbstractDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        super.execute(execution);
        MyDTO dto = new MyDTO("created by Before service task");
        SpinJsonNode json = JSON(dto);
        log("DTO serialized with SPIN: " + json.toString());
        execution.setVariable("dto", json);
        log("Stored SPIN output into execution under name 'dto'");

        execution.setVariable("dto2", dto);
    }

}
