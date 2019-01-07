package com.sheena.playground.logic.activities;

import java.util.List;


public interface ActivityService {
		
	public void cleanup();
	
	public ActivityEntity getActivityByType(String type) throws ActivityTypeNotSupportedException;
	
	public List<ActivityEntity> getAllActivities (int size, int page);

	public Object addNewActivity(ActivityEntity activityEntity, String userPlayground, String email)
			throws ActivityTypeNotSupportedException, ActivityWithNoTypeException;

	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException;
	
}
