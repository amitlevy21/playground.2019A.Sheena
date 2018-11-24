package com.sheena.playground.logic.dao.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.dao.ElementDao;
import com.sheena.playground.logic.entity.ElementEntity;

/**
 * DummyElementDao
 */
public class DummyElementDao implements ElementDao {


    private Map<String, ElementEntity> idToElement;

    public DummyElementDao() {
        idToElement = new HashMap<>();
    }

    @Override
    public List<ElementEntity> getAllElements(int size, int page) {
        return idToElement.values().stream().skip(size * page).limit(size).collect(Collectors.toList());
    }

    @Override
    public ElementEntity getElementById(String id) throws ElementNotExistException {
        ElementEntity et = idToElement.get(id);
        if (et == null)
            throw new ElementNotExistException();
        return et;
    }

    @Override
    public boolean removeElementById(String id) {
        return idToElement.remove(id) != null ? true : false;
    }

    @Override
    public void updateElement(ElementEntity element) {
        idToElement.put(element.getId(), element);
    }

    @Override
    public void addElement(ElementEntity element) {
        idToElement.put(element.getId(), element);
    }

    @Override
    public List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
            throws ElementNotExistException {
        List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : idToElement.values()) {
            Double distanceX = Math.abs(e.getLocation().getX() - x);
            Double distanceY = Math.abs(e.getLocation().getY() - y);
            if (distance.compareTo(distanceX) >= 0 && distance.compareTo(distanceY) >= 0) {
                rv.add(e);
            }
        }
        if (rv.isEmpty())
            throw new ElementNotExistException();
        return rv;
    }

    @Override
    public List<ElementEntity> getElementsAttribute(String attributeName, Object value)
            throws ElementNotExistException {
        List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : idToElement.values()) {
            if (e.getAttributes().get(attributeName).toString().equals(value.toString())) {
                rv.add(e);
            }
        }
        if (rv.isEmpty())
            throw new ElementNotExistException();
        return rv;
    }

}