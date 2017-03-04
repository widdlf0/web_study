package com.dasol.member.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.dasol.jdbc.ConnectionProvider;
import com.dasol.jdbc.JdbcUtil;
import com.dasol.member.dao.MemberDAO;
import com.dasol.member.model.Member;

public class JoinService {

	private MemberDAO memberDAO = new MemberDAO();

	public void join(JoinRequest joinRequest) {
		Connection conn = null;
		Member member = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
	
			member = memberDAO.selectByEmail(conn, joinRequest.getEmail());
			
			if (member != null) {
				JdbcUtil.rollback(conn);
				throw new DuplicateIdException();
			}
			
			memberDAO.insertData(conn, 
					new Member(joinRequest.getEmail(), 
							joinRequest.getPassword(),
							null, // 유저 닉네임 생성
							new Date(), 
							null, // 유저 기본 프로필 설정
							null, // 이메일 인증 코드
							false)); // 이메일 인증 체크 기본 false
			conn.commit();
			
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException();
		} finally {
			JdbcUtil.close(conn);
		}
	}
}