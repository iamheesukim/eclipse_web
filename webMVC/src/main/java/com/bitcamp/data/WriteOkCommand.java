package com.bitcamp.data;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bitcamp.home.CommandService;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class WriteOkCommand implements CommandService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ������ ���ε� �Ǵ� ��ġ�� �����ּҸ� ���ϳ�.
		String path = request.getServletContext().getRealPath("/upload");
		System.out.println("path = "+path);
		
		//���� ���ε�� ���� �����͸� ������ �������� Ŭ����
		// 1.request 2.���Ͼ��ε���ġ
		// 3.���ε尡�����������ִ�ũ��(byte) 1024*1024*1024
		// 4.�ѱ����ڵ�
		// 5.���ϸ��� �ߺ��� �� ���ϸ��� Rename ���ִ� Ŭ������ ��ü�� ������ش�.
		int maxSize = 1024*1024*1024;
		DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
		MultipartRequest mr = new MultipartRequest(request, path, maxSize, "UTF-8", policy);
		
		DataVO vo = new DataVO();
		vo.setTitle(mr.getParameter("title"));
		vo.setContent(mr.getParameter("content"));
		System.out.println("title ==> "+vo.getTitle());
		System.out.println("content ==> "+vo.getContent());
		
		HttpSession ses = request.getSession();
		vo.setUserid((String)ses.getAttribute("userid"));
		
		//���ϸ�ó��
		//1.���� name ������ ������
		Enumeration fileList = mr.getFileNames(); //filename
		
		while(fileList.hasMoreElements()) {
			String fileAttr = (String)fileList.nextElement();
			//����� ���� �̸� ��������
			String newFilename = mr.getFilesystemName(fileAttr);
			System.out.println("���ϸ� ==> "+newFilename);
			vo.setFilename(newFilename);
				
		}
		
		//�����ͺ��̽� ���ڵ� �߰�
		DataDAO dao = new DataDAO();
		int result = dao.dataInsert(vo);
		
		if(result<=0) {//���� ��ϵ��� �ʾ��� ��
			//���� ���� �� �۾���(history)
								//���	���ϸ�
			File file = new File(path, vo.getFilename());
			file.delete();
		}
		
		//					������		��
		request.setAttribute("cnt", result);
		//�̷��鼭 dataWriteOk.jsp ���� cnt��� ���������� result�� �� �� ����
				
		return "dataWriteOk.jsp";
	}

}