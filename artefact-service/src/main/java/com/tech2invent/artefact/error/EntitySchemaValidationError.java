package com.tech2invent.artefact.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EntitySchemaValidationError extends ApiSubError  {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String artefactId;
    private String type;
    private String code;
    private String path;
    private String schemaPath;
    private String[] arguments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> details;
    private String message;

    public EntitySchemaValidationError(ValidationMessage validationMessage) {
        type = validationMessage.getType();
        code = validationMessage.getCode();
        path = validationMessage.getPath();
        schemaPath = validationMessage.getSchemaPath();
        arguments = validationMessage.getArguments();
        details = validationMessage.getDetails();
        message = validationMessage.getMessage();
    }

    public EntitySchemaValidationError(String artefactId, ValidationMessage validationMessage) {
        this.artefactId = artefactId;
        type = validationMessage.getType();
        code = validationMessage.getCode();
        path = validationMessage.getPath();
        schemaPath = validationMessage.getSchemaPath();
        arguments = validationMessage.getArguments();
        details = validationMessage.getDetails();
        message = validationMessage.getMessage();
    }


}
