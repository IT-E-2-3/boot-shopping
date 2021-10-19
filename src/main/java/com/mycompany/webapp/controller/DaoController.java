package com.mycompany.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.webapp.dto.Board;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/dao")
@Slf4j
public class DaoController {
	
	@Resource
	private BoardService boardService;
	
	@RequestMapping("/content")
	public String content() {
		log.info("실행");
		return "dao/content";
	}
	
	@RequestMapping("/boardList")
	public String boardList(@RequestParam(defaultValue="1") int pageNo, Model model) {
	   log.info("실행");
	   
	   int totalRows = boardService.getTotalBoardNum();
	   if(totalRows < 1000) {
	      for(int i=1; i<=1000; i++) {
	         Board board = new Board();
	         board.setBtitle("제목"+i);
	         board.setBcontent("내용"+i);
	         board.setMid("user");
	         boardService.writeBoard(board);
	      }
	   }
	   
	   totalRows = boardService.getTotalBoardNum();
	   Pager pager = new Pager(5, 5, totalRows, pageNo);   
	   model.addAttribute("pager", pager);
	   
	   List<Board> boards = boardService.getBoards(pager);
	   model.addAttribute("boards", boards);
	   return "dao/boardList";
	}
	
	@GetMapping("/boardWriteForm")
	public String boardWriteForm() {
	   log.info("실행");
	   return "dao/boardWriteForm";
	}
	
	@PostMapping("/boardWrite")
	public String boardWrite(Board board) throws Exception{
	   log.info("실행");
	   
	   //첨부파일이 있냐 없냐를 검사
	   //우리는 db에 multipartfile형태로 집어넣지 않고 string 세 개로 쪼개서 넣는다
	   //첨부파일이 있으면
	   if(board.getBattach() != null && !board.getBattach().isEmpty()) {
		  MultipartFile mf = board.getBattach();
		  board.setBattachoname(mf.getOriginalFilename()); //파일의 원래 이름
		  board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename()); //파일 저장하는 이름
		  board.setBattachtype(mf.getContentType()); //확장자 (?)
		  File file = new File("/Users/go/hyundai_itne/upload_files/" + board.getBattachsname());
		  mf.transferTo(file); //예외처리해줘야 함.
	   }
	   
	   boardService.writeBoard(board);
	   
	   return "redirect:/dao/boardList";
	}
	
	@GetMapping("/boardDetail")
	public String boardDetail(int bno, Model model) {
	   log.info("실행");
	   Board board = boardService.getBoard(bno);
	   model.addAttribute("board", board);
	   return "dao/boardDetail";
	}
	
	//파일 다운로드를 할 수 있게 해주는 메서드
	@GetMapping("/battachDownload")
	public void battachDownload(int bno, HttpServletResponse res)throws Exception{
		//직접 응답을 만들기 때문에 메서드 타입은 void
		Board board = boardService.getBoard(bno);
        String battachoname = board.getBattachoname();
        if(battachoname == null) return;
        
        //한글
        battachoname = new String(battachoname.getBytes("UTF-8"),"ISO-8859-1");
        String battachsname = board.getBattachsname();      
        String battachspath = "/Users/go/hyundai_itne/upload_files/" + battachsname;
        String battachtype = board.getBattachtype();
  
        //파일로 다운받고 싶으면 추가
        res.setHeader("Content-Disposition", "attachment; filename=\""+battachoname+"\";");
        //-> 안써주면 사진을 볼 수만 있음
        res.setContentType(battachtype);

        InputStream is = new FileInputStream(battachspath);
        OutputStream os = res.getOutputStream();
        FileCopyUtils.copy(is, os);
        is.close();
        os.flush();
        os.close();
	}
	
	@GetMapping("/boardUpdateForm")
	public String boardUpdateForm(int bno, Model model) {
	   log.info("실행");
	   Board board = boardService.getBoard(bno);
	   model.addAttribute("board", board);
	   return "dao/boardUpdateForm";
	}
	
	 @PostMapping("/boardUpdate")
	 public String boardUpdate(Board board) throws Exception {
	    log.info("실행");
	    
	    if(board.getBattach() != null && !board.getBattach().isEmpty()) {
	       MultipartFile mf = board.getBattach();
	       board.setBattachoname(mf.getOriginalFilename());
	       board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
	       board.setBattachtype(mf.getContentType());
	       File file = new File("/Users/go/hyundai_itne/upload_files/" + board.getBattachsname());
	       mf.transferTo(file);
	    }
	    
	    boardService.updateBoard(board);
	    return "redirect:/dao/boardDetail?bno=" + board.getBno();
	 }
	 
	 @GetMapping("/boardDelete")
	 public String boardDelete(int bno) {
	    log.info("실행");
	    boardService.removeBoard(bno);
	    return "redirect:/dao/boardList";
	 }
}
