package com.andreistraut.drp.core.model;

import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ValidationErrorLogTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidationErrorLogEmptyLog() {
	ValidationErrorLog errorLog = new ValidationErrorLog();	
	Assert.assertTrue("Expected empty log, but was not", errorLog.getValidationErrors().isEmpty());
    }
    
    @Test
    public void testValidationErrorLogAddErrorToNullCollection() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.setValidationErrors(null);
	errorLog.addValidationError("Error1");
	
	Assert.assertTrue("Expected 1 validation error, but was different", errorLog.getValidationErrors().size() == 1);
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrors().iterator().next().equals("Error1"));
    }
    
    @Test
    public void testValidationErrorLogAddErrorToEmptyCollection() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	errorLog.addValidationError("Error1");
	
	Assert.assertTrue("Expected 1 validation error, but was different", errorLog.getValidationErrors().size() == 1);
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrors().iterator().next().equals("Error1"));
    }
    
    @Test
    public void testValidationErrorLogAddError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	List<String> errors = Lists.newArrayList("Error1", "Error2");
	errorLog.setValidationErrors(errors);
	
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected 3 validation errors, but was different", errorLog.getValidationErrors().size() == 3);
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrors().contains("Error3"));
    }
    
    @Test
    public void testValidationErrorLogAddEmptyError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("");
	
	Assert.assertTrue("Expected empty validation log, but was not", errorLog.getValidationErrors().isEmpty());
    }
    
    @Test
    public void testValidationErrorLogAddSpaceCharacterError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError(" ");
	
	Assert.assertTrue("Expected empty validation log, but was not", errorLog.getValidationErrors().isEmpty());
    }
    
    @Test
    public void testValidationErrorLogAddNullError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError(null);
	
	Assert.assertTrue("Expected empty validation log, but was not", errorLog.getValidationErrors().isEmpty());
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsReadOnly() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	exception.expect(UnsupportedOperationException.class);
	errorLog.getValidationErrors().add("SomeError");
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsNumber() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected 3 validation errors, but was different", errorLog.getValidationErrors().size() == 3);
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsContents() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected validation error not present", errorLog.getValidationErrors().contains("Error1"));
	Assert.assertTrue("Expected validation error not present", errorLog.getValidationErrors().contains("Error2"));
	Assert.assertTrue("Expected validation error not present", errorLog.getValidationErrors().contains("Error3"));
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsEmptyAfterNullSet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(null);
	
	Assert.assertTrue("Expected empty collection, but was different", errorLog.getValidationErrors().isEmpty());
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsEmptyAfterEmptySet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected empty collection, but was different", 
		errorLog.getValidationErrors().isEmpty());
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsStringEmpty() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	Assert.assertTrue("Expected Optional.empty, but was different", 
		!errorLog.getValidationErrorsString().isPresent());
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsStringEmptyAfterEmptySet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected Optional.empty, but was different", 
		!errorLog.getValidationErrorsString().isPresent());
    }

    @Test
    public void testValidationErrorLogGetValidationErrorsStringEmptyAfterNullSet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(null);
	
	Assert.assertTrue("Expected Optional.empty, but was different", 
		!errorLog.getValidationErrorsString().isPresent());
    }

    @Test
    public void testValidationErrorLogGetStringValidationErrorsContents() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrorsString().get().contains("Error1"));
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrorsString().get().contains("Error2"));
	Assert.assertTrue("Expected validation error not present", 
		errorLog.getValidationErrorsString().get().contains("Error3"));
    }

    @Test
    public void testValidationErrorLogGetFirstValidationError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected validation error not first", 
		errorLog.getFirstValidationError().get().equals("Error1"));
    }

    @Test
    public void testValidationErrorLogGetFirstValidationErrorEmpty() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getFirstValidationError().isPresent());
    }

    @Test
    public void testValidationErrorLogGetFirstValidationErrorEmptyAfterEmptySet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getFirstValidationError().isPresent());
    }

    @Test
    public void testValidationErrorLogGetFirstValidationErrorEmptyAfterNullSet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getFirstValidationError().isPresent());
    }

    @Test
    public void testValidationErrorLogGetLastValidationError() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	
	Assert.assertTrue("Expected validation error not last", 
		errorLog.getLastValidationError().get().equals("Error3"));
    }

    @Test
    public void testValidationErrorLogGetLastValidationErrorEmpty() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getLastValidationError().isPresent());
    }

    @Test
    public void testValidationErrorLogGetLastValidationErrorEmptyAfterEmptySet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getLastValidationError().isPresent());
    }

    @Test
    public void testValidationErrorLogGetLastValidationErrorEmptyAfterNullSet() {
	ValidationErrorLog errorLog = new ValidationErrorLog();
	
	errorLog.addValidationError("Error1");
	errorLog.addValidationError("Error2");
	errorLog.addValidationError("Error3");
	errorLog.setValidationErrors(Lists.<String>newArrayList());
	
	Assert.assertTrue("Expected Optional.absent error but was not", 
		!errorLog.getLastValidationError().isPresent());
    }
}
