package com.mycompany.smp.exception;

import com.mycompany.smp.dto.ErrorDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    private List<ErrorDTO> errors;
    public BusinessException(List<ErrorDTO> errors) {
        this.errors = errors;
    }
}
