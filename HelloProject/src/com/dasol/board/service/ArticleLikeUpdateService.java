package com.dasol.board.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.dasol.board.dao.ArticleDAO;
import com.dasol.board.model.Article;
import com.dasol.board.model.ArticleLike;
import com.dasol.jdbc.ConnectionProvider;
import com.dasol.jdbc.JdbcUtil;

public class ArticleLikeUpdateService {
	ArticleDAO articleDAO = new ArticleDAO();

	public ArticleLikeData likeUpdate(ArticleLike articleLike) {
		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();

			Article article = articleDAO.selectById(conn, articleLike.getArticleNo());

			if (article == null) {
				throw new ArticleNotFoundException();
			}

			ArticleLike savedArticleLike = articleDAO.insertLike(conn, articleLike.getMemberId(),
					articleLike.getNickname(), article.getNumber());

			List<ArticleLike> articleLikeList = articleDAO.getArticleLikeList(conn, savedArticleLike.getArticleNo());

			updateArticleLikeCnt(conn, savedArticleLike.getArticleNo(), articleLikeList.size());
			
			return new ArticleLikeData(savedArticleLike.getNumber(), savedArticleLike.getMemberId(),
					savedArticleLike.getNickname(), articleLikeList.size());

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public int likeDelete(int likeNo, int articleNo) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();

			ArticleLike articleLike = articleDAO.getArticleLike(conn, likeNo);

			if (articleLike == null) {
				throw new ArticleLikeNotFoundException();
			}

			articleDAO.deleteArticleLike(conn, likeNo);
			
			List<ArticleLike> articleLikeList = articleDAO.getArticleLikeList(conn, articleNo);
			
			updateArticleLikeCnt(conn, articleNo, articleLikeList.size());

			return articleLikeList.size();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public boolean isLikeIt(int articleNo, int memberId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			List<ArticleLike> articleLikeList = articleDAO.getArticleLikeList(conn, articleNo);

			for (ArticleLike articleLike : articleLikeList) {
				if (articleLike.isLikeIt(memberId)) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
	private void updateArticleLikeCnt(Connection conn, int no, int totLikeCnt) throws SQLException {
		articleDAO.updateLikeCnt(conn, no, totLikeCnt);
	}

}