
package com.andreistraut.drp.core.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ValidationErrorLog {
    
    private List<String> validationErrors;
    
    public ValidationErrorLog() {
	this.validationErrors = Lists.<String> newArrayList();
    }
    
    public List<String> getValidationErrors() {
	if(this.validationErrors == null) {
	    this.validationErrors = Lists.<String> newArrayList();
	}
	
	return Collections.unmodifiableList(this.validationErrors);
    }
    
    public Optional<String> getValidationErrorsString() {
	if(this.validationErrors == null) {
	    return Optional.empty();
	}
	
	if(this.validationErrors.isEmpty()) {
	    return Optional.empty();
	}
	
	return Optional.of(String.join(System.lineSeparator(), this.validationErrors));
    }
    
    public Optional<String> getFirstValidationError() {
	List<String> errors = this.getValidationErrors();
	
	if(errors.isEmpty()) {
	    return Optional.empty();
	}
	
	return Optional.of(errors.iterator().next());
    }
    
    public Optional<String> getLastValidationError() {
	List<String> errors = this.getValidationErrors();
	
	if(errors.isEmpty()) {
	    return Optional.empty();
	}
	
	return Optional.of(errors.get(errors.size() - 1));
    }
    
    public void setValidationErrors(List<String> errors) {
	this.validationErrors = errors;
    }
    
    public void addValidationError(String error) {
	if(error == null || error.trim().replace(" ", "").equals("")) {
	    return;
	}
	
	if(this.validationErrors == null) {
	    this.validationErrors = Lists.<String> newArrayList();
	}
	
	this.validationErrors.add(error);
    }
}
