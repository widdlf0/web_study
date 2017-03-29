package com.dasol.board.command;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.dasol.board.model.ArticleLike;
import com.dasol.board.service.ArticleLikeData;
import com.dasol.board.service.ArticleLikeUpdateService;
import com.dasol.mvc.command.CommandHandler;
import com.dasol.noti.model.MyNotification;
import com.dasol.noti.service.WriteMyNotiService;

public class ArticleLikeUpdateHandler implements CommandHandler {
	
	private ArticleLikeUpdateService service = new ArticleLikeUpdateService();
	private WriteMyNotiService writeMyNotiService = new WriteMyNotiService();
	
	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("likeHandler");
		
		String memberId = request.getParameter("member_id");
		String nickname = request.getParameter("nickname");
		String articleNo = request.getParameter("article_no");
		String articleUserId = request.getParameter("article_userId");
		
		int id = Integer.parseInt(memberId);
		int no = Integer.parseInt(articleNo);
		int myId = Integer.parseInt(articleUserId);
		
		ArticleLike articleLike = new ArticleLike(null, id, nickname, no);
		ArticleLikeData articleLikeData = service.likeUpdate(articleLike);
		
		MyNotification myNoti 
			= new MyNotification(null, no, id, "like", false, new Date(), myId);
		writeMyNotiService.writeMyNoti(myNoti);
			
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("like_no", articleLikeData.getLikeNo());
		jsonObj.put("member_id", articleLikeData.getMemberId());
		jsonObj.put("nickname", articleLikeData.getNickname());
		jsonObj.put("totLikeCnt", articleLikeData.getTotLikeCnt());
		
		response.setContentType("text/html;charset=UTF-8"); 
		PrintWriter out;
		out = response.getWriter();
		out.print(jsonObj.toString());
		
		return null;
	}
	
}