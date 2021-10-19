package com.mycompany.webapp.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.BoardDao;
import com.mycompany.webapp.dto.Board;
import com.mycompany.webapp.dto.Pager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BoardService {
	
	@Resource
	private BoardDao boardDao;
	
	public List<Board> getBoards(Pager pager) {
		log.info("실행");
		return boardDao.selectByPage(pager);
	}
	
	public Board getBoard(int bno) {
		log.info("실행");
		return boardDao.selectByBno(bno);
	}
	
	public int getTotalBoardNum() {
		log.info("실행");
		return boardDao.count();
	}
	
	public void writeBoard(Board board) {
		log.info("실행");
		boardDao.insert(board);
	}
	
	public void updateBoard(Board board) {
		log.info("실행");
		boardDao.update(board);
	}
	
	public void removeBoard(int bno) {
		log.info("실행");
		boardDao.deleteByBno(bno);
	}
}








