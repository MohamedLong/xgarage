package com.xgarage.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xgarage.app.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusDto {

    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Status status;
}
