package com.writing;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.util.CustomInfo;
import com.util.DBConn;
import com.util.MyPage;

public class WritingServelt extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
		RequestDispatcher rd =
				req.getRequestDispatcher(url);

		rd.forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String cp = req.getContextPath();	//http://localhost:8080/������Ʈ��
		String uri = req.getRequestURI();	//study/bbs/created.do
		//DB����

		Connection conn = DBConn.getConnection();

		WritingDAO dao = new WritingDAO(conn);		
		
		//����¡ó���� ���� ��ü ����

		MyPage myPage = new MyPage();	
		
		String url;	
		
		//uri�� �ش� �ܾ �ִ��� Ȯ��

		if(uri.indexOf("created.do")!=-1){
			
			HttpSession session = req.getSession();
			
			CustomInfo info =(CustomInfo)session.getAttribute("customInfo");
			
			if(info==null) {
				
				url="/writing/created.jsp";
				forward(req, resp, url);
				return;
				
			}
			
			url = "/writing/created.jsp";
			forward(req, resp, url);
			//�Խñ� �ۼ��� ����ҽ�
		}else if(uri.indexOf("created_ok.do")!=-1){
			
			WritingDTO dto = new WritingDTO();
			int maxNum = dao.getMaxNum();
			
			dto.setNum(maxNum+1);
			dto.setSubject(req.getParameter("subject"));
			dto.setName(req.getParameter("name"));
			dto.setEmail(req.getParameter("email"));
			dto.setPw(req.getParameter("pw"));
			dto.setContent(req.getParameter("content"));
	
			dao.insertData(dto);
			//redirect ����
			url = cp+"/bbs/list.do";
			resp.sendRedirect(url);		
			
		}else if(uri.indexOf("list.do")!=-1){
			
			String pageNum = req.getParameter("pageNum");
			
			int currentPage = 1;
			
			if(pageNum!=null) {
				currentPage = Integer.parseInt(pageNum);
				
			}
			
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue==null || searchValue.equals(null)){
				searchKey ="subject";
				searchValue = "";
			}else{
				if(req.getMethod().equalsIgnoreCase("GET")){
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
				}
			}
			
			int dataCount = dao.getDataCount(searchKey, searchValue);
			int numPerPage = 7;
			int totalPage = myPage.getPagecount(numPerPage, dataCount);			
			
			if(currentPage>totalPage) {
				currentPage = totalPage;
			}
			
			int start = (currentPage-1)*numPerPage+1;
			int end = currentPage*numPerPage;
			
			List<WritingDTO> lists = dao.getLists(start, end, searchKey, searchValue);
			
			String param = "";
			if(searchValue!=null&&!searchValue.equals("")){
				param = "searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
			
			String listUrl = cp+"/bbs/list.do";
			if(!param.equals("")){
				listUrl +="?" + param;
			}

			String pageIndexList = myPage.pageIndexList(currentPage, totalPage, listUrl);
			
			String articleUrl = cp + "/bbs/article.do?pageNum=" + currentPage;
			
			if(!param.equals("")){
				articleUrl += "&" + param;
			}
			
			//�������� ������

			req.setAttribute("lists", lists); //���� ���� ������ ������
			req.setAttribute("pageIndexList", pageIndexList);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("dataCount", dataCount);			
									
			url = "/writing/list.jsp";
			forward(req, resp, url);
			//�Խñ� �ۼ��� ����ҽ�
		}else if(uri.indexOf("article.do")!=-1) {
			
			

			int num = Integer.parseInt(req.getParameter("num"));
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");
			
			if(searchValue!=null&&!searchValue.equals("")){
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}			
			
			//��ȸ�� ����
			dao.updateHitCount(num);
			
			//�Խ��� ���� �б�
			WritingDTO dto = dao.getReadData(num);
			
			//�Խ��� �ش� ���� ���� ��� redirect
			if(dto==null){
				url = cp + "/bbs/list.do";
				resp.sendRedirect(url);
			}			
			
			//�������
			int lineSu = dto.getContent().split("\n").length;
			
			dto.setContent(dto.getContent().replaceAll("\r", "<br/>"));
			
			String param = "pageNum=" + pageNum;
			
			if(searchValue!=null&&!searchValue.equals("")){
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
			
			req.setAttribute("dto", dto);
			req.setAttribute("params", param);
			req.setAttribute("lineSu", lineSu);	
			req.setAttribute("pageNum", pageNum);
			
			//������ ����
			url = "/writing/article.jsp";
			forward(req, resp, url);

			//������Ʈ
		}else if(uri.indexOf("updated.do")!=-1){

			int num = Integer.parseInt(req.getParameter("num"));
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null){
				searchValue =URLDecoder.decode(searchValue, "UTF-8");
			}

			//�Խ��� ���� �б�
			WritingDTO dto = dao.getReadData(num);
			
			if(dto==null){
				url = cp + "/bbs/list.do";
				resp.sendRedirect(url);
			}
			
			String param = "pageNum="+pageNum;
			
			if(searchValue!=null&&!searchValue.equals("")){
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
		
			req.setAttribute("dto", dto);
			req.setAttribute("pageNum", pageNum);
			req.setAttribute("params", param);
			req.setAttribute("searachKey", searchKey);
			req.setAttribute("searchValue", searchValue);
			
			//������ ����
			url = "/writing/updated.jsp";
			forward(req, resp, url);
				
		}else if(uri.indexOf("updated_ok.do")!=-1){
		
			int num = Integer.parseInt(req.getParameter("num"));
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			System.out.print(searchKey);
			if(searchValue!=null){
				searchValue =URLDecoder.decode(searchValue, "UTF-8");
			}
			
			WritingDTO dto = new WritingDTO();

			dto.setNum(Integer.parseInt(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setName(req.getParameter("name"));
			dto.setEmail(req.getParameter("email"));
			dto.setPw(req.getParameter("pw"));
			dto.setContent(req.getParameter("content"));			
			
			//�Խñ� ����
			dao.updateData(dto);
			
			String param = "pageNum="+ pageNum;
			
			if(searchValue!=null&&!searchValue.equals("")){
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
            
            //�Խ��� ���� �Ϸ� �� ����Ʈ �������� redirect
			url = cp + "/bbs/list.do?" +param;
			resp.sendRedirect(url);
			
		}else if(uri.indexOf("deleted_ok.do")!=-1){
			
			int num = Integer.parseInt(req.getParameter("num"));
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null&&!searchValue.equals("")){
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			//�Խñ� ����
			dao.deleteData(num);
			
			String param = "pageNum="+pageNum;
			
			if(searchValue!=null&&!searchValue.equals("")){
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
            
            //�Խ��� ���� �Ϸ� �� ����Ʈ �������� redirect
			url = cp + "/bbs/list.do?"+param;

			resp.sendRedirect(url);

		}
	}
}	


