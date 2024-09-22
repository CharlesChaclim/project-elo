package br.com.project.errors;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class StanderError {

    private String path;
    private String error;
    private Integer status;
    private LocalDateTime timestamp;


    public StanderError(LocalDateTime timestamp, Integer status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }
}
