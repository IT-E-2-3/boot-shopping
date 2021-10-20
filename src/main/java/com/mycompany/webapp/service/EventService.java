package com.mycompany.webapp.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.EventDao;
import com.mycompany.webapp.dto.EventDto;



@Service
public class EventService {

	@Resource
	EventDao eventdao;
	
	public EventDto getEventContent() {
		return eventdao.getEvent();
	}
	
	public int getEamount() {
		return eventdao.getEamount();
	}
}
