package com.mycompany.webapp.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycompany.webapp.dto.EventDto;


@Mapper
public interface EventDao {
	public EventDto getEvent();
	public int getEamount();
}
