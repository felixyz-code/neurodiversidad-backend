package com.neurodiversidad.neurodiversidad_backend.patient;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {

    /**
	 * Valor serialVersionUID de tipo long
	 */
	private static final long serialVersionUID = 7727990390423105483L;

	public PatientNotFoundException(String message) {
        super(message);
    }
}
