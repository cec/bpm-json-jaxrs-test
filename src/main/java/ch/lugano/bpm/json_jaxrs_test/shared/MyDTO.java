package ch.lugano.bpm.json_jaxrs_test.shared;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Dummy POJO for testing
 */
public class MyDTO {

    private String name;
    private Date date;
    private LocalDateTime timeStamp;

    public MyDTO() {
    }

    public MyDTO(String name) {
        this.name = name;
        this.date = new Date();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
