package com.sheena.playground.logic.elements.jpa;

import java.util.Date;
import java.util.List;

import com.sheena.playground.aop.IsUserManager;
import com.sheena.playground.dal.ElementDao;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;
import com.sheena.playground.logic.users.Roles;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaElementService implements ElementService {

	private ElementDao elementDao;
	private UsersService usersService;
	private String playgroundName;

	@Value("${playground.name:defaultPlayground}")
	public void setPlaygroundName(String playgroundName) {
		this.playgroundName = playgroundName;
	}
	
	@Autowired
	public JpaElementService(ElementDao elementDao, UsersService usersService) {
		this.elementDao = elementDao;
		this.usersService = usersService;
	}

	@Override
	@Transactional
	@IsUserManager
	public ElementEntity addNewElement(String creatorEmail, ElementEntity element) {
		element.setPlayground(this.playgroundName);
		return this.elementDao.save(element);
	}

	@Override
	@Transactional
	@IsUserManager
	public void updateElement(String updaterEmail, String id, ElementEntity entityUpdates) throws ElementNotExistException {
		ElementEntity existing = this.getElementById(id);
		
		if(entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().equals(existing.getAttributes()))
			existing.setAttributes(entityUpdates.getAttributes());
		if(entityUpdates.getCreationDate() != null & !entityUpdates.getCreationDate().equals(existing.getCreationDate()))
			existing.setCreationDate(entityUpdates.getCreationDate());
		if(entityUpdates.getCreatorEmail() != null && !entityUpdates.getCreatorEmail().equals(existing.getCreatorEmail()))
			existing.setCreatorEmail(entityUpdates.getCreatorEmail());
		if(entityUpdates.getCreatorPlayground() != null && !entityUpdates.getCreatorPlayground().equals(existing.getCreatorPlayground()))
			existing.setCreatorPlayground(entityUpdates.getCreatorPlayground());
		if(entityUpdates.getExpirationDate() != null && !entityUpdates.getExpirationDate().equals(existing.getExpirationDate()))
			existing.setExpirationDate(entityUpdates.getExpirationDate());
		if(entityUpdates.getName() != null && !entityUpdates.getName().equals(existing.getName()))
			existing.setName(entityUpdates.getName());
		if(entityUpdates.getType() != null && !entityUpdates.getType().equals(existing.getType()))
			existing.setType(entityUpdates.getType());
		if(entityUpdates.getX() != null && !entityUpdates.getX().equals(existing.getX()))
			existing.setX(entityUpdates.getX());
		if(entityUpdates.getY() != null && !entityUpdates.getY().equals(existing.getY()))
			existing.setY(entityUpdates.getY());
		
		this.elementDao.save(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public ElementEntity getElementById(String id) throws ElementNotExistException {
		return this.elementDao.findById(id)
				.orElseThrow(() -> new ElementNotExistException("no element with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> getAllElements(String requestorEmail, int size, int page) throws UserDoesNotExistException {
		String requestorRole = getUserRole(requestorEmail);
		if(requestorRole.equalsIgnoreCase(Roles.PLAYER.toString())) {
			return this.elementDao.findByExpirationDateAfter(new Date(), PageRequest.of(page, size));
		}
		Page<ElementEntity> elementsPage = elementDao.findAll(PageRequest.of(page, size));
		return elementsPage.getContent();
//		Page<ElementEntity> elementsPage = elementDao.findAll(PageRequest.of(page, size));
//		List<ElementEntity> allElements = elementsPage.getContent();
//		
//		if(requestorRole.equalsIgnoreCase(Roles.PLAYER.toString())) {
//			Date currentTime = new Date(); // for comparing the elements expiration date
//			
//			allElements = allElements.stream().filter(element -> element.getExpirationDate().after(currentTime)).collect(Collectors.toList());
//			return getAllElementsForPlayer(size, allElements, elementsPage, currentTime);
//		}
//		// else the requestor's role is a manager, meaning we can return all elements
//		return allElements;
	}

	@Override
	public List<ElementEntity> getElementsNearCoordinates(String requestorEmail, Double x, Double y, Double distance, int size, int page) throws UserDoesNotExistException {
		String requestorRole = getUserRole(requestorEmail);
		
		if(requestorRole.equalsIgnoreCase(Roles.PLAYER.toString())) {
			Date currentTime = new Date(); // for comparing the elements expiration date
			return elementDao.findByXBetweenAndYBetweenAndExpirationDateAfter(
					x - distance, 
					x + distance, 
					y - distance, 
					y + distance, 
					currentTime, 
					PageRequest.of(page, size));
		}
		
		return elementDao.findByXBetweenAndYBetween(
				x - distance, 
				x + distance, 
				y - distance, 
				y + distance,
				PageRequest.of(page, size));
	}

	@Override
	public List<ElementEntity> getElementsAttribute(String requestorEmail, String attributeName, Object value, int size, int page)
			throws ElementNotExistException, NoSuceElementAttributeException, UserDoesNotExistException {
		String requestorRole = getUserRole(requestorEmail);
		Date currentTime = new Date(); // for comparing the elements expiration date
		
		switch (attributeName) {
		case "name": {
			if(requestorRole.equalsIgnoreCase(Roles.PLAYER.toString()))
				return elementDao.findByNameEqualsAndExpirationDateAfter(value.toString(), currentTime, PageRequest.of(page, size));
			return elementDao.findByNameEquals(value.toString(), PageRequest.of(page, size));
		}

		case "type": {
			if(requestorRole.equalsIgnoreCase(Roles.PLAYER.toString()))
				return elementDao.findByTypeEqualsAndExpirationDateAfter(value.toString(), currentTime, PageRequest.of(page, size));
			return elementDao.findByTypeEquals(value.toString(), PageRequest.of(page, size));
		}
		default:
			throw new NoSuceElementAttributeException("attribute: " + attributeName + " is not supported");
		}
	}

	@Override
	public void cleanup() {
		this.elementDao.deleteAll();
	}
	
	private String getUserRole(String userEmail) throws UserDoesNotExistException {
		return usersService.getUserByEmail(userEmail).getRole();
	}
}
