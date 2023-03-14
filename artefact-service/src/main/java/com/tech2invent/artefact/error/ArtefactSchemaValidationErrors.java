package com.tech2invent.artefact.error;

import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArtefactSchemaValidationErrors extends ApiSubError{

    private String artefactId;
    private Set<ValidationMessage> validationErrors;
}
