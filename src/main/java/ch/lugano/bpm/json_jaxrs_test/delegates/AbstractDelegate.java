package ch.lugano.bpm.json_jaxrs_test.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDelegate implements JavaDelegate {

    protected DelegateExecution execution;
    protected Logger logger;


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        setExecution(execution);
    }

    protected void log(String message) {
        if (null == logger)
            logger = LoggerFactory.getLogger(this.getClass().getName());
        logger.info(message);
    }

    private void setExecution(DelegateExecution execution) {
        this.execution = execution;
    }

}
