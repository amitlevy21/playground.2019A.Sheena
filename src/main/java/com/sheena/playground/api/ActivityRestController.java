package com.sheena.playground.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sheena.playground.logic.ActivityAlreadyExistsException;
import com.sheena.playground.logic.ActivityNotFoundException;
import com.sheena.playground.logic.ActivityService;
import com.sheena.playground.logic.ActivityTypeNotAllowedException;

@RestController
public class ActivityRestController {
	
	private ActivityService activityService;
	
	@Autowired
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}
	
    @RequestMapping(
        method=RequestMethod.POST,
        path="/playground/activities/{userPlayground}/{email}",
        produces=MediaType.APPLICATION_JSON_VALUE,
        consumes=MediaType.APPLICATION_JSON_VALUE)
    public Object addNewActivity(
        @PathVariable("userPlayground") String userPlayground,
        @PathVariable("email") String email,
        @RequestBody ActivityTO newActivityTO) throws ActivityNotFoundException, ActivityAlreadyExistsException, ActivityTypeNotAllowedException {
    	  	
    	//TODO: Once there is logic layer - an update to the DB will be required
    	
    	// TODO: add checks for the path variables equals to the json fields
    	
    	return new ActivityTO(
    				this.activityService.addNewActivity(
    						newActivityTO.toActivityEntity()));
    }
}