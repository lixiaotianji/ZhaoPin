package com.zhaopin.client.controller;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.zhaopin.client.server.ResumeService;
import com.zhaopin.client.server.UserServer;
import com.zhaopin.po.Resume;
import com.zhaopin.po.User;
import com.zhaopin.utils.FilePath;

@Controller()
@RequestMapping("/client")
public class ResumeController  {
	@Resource(name="resumeServiceImpl")
	private ResumeService  resumeService;

	@Resource(name="userServerImpl")
	private UserServer userSever;


	@RequestMapping("/resume")
	public String resume(){


		return "client/resume";
	}
	/**
	 * 保存个人简历
	 * @param req
	 * @param resume
	 * @param request
	 * @param session
	 * @return
	 * @throws IOException
	 */

	@RequestMapping("/resume/save")
	public String save( HttpServletRequest req , @ModelAttribute Resume resume,HttpServletRequest request,HttpSession session) throws IOException{
		User user=(User)session.getAttribute("user");

		boolean bool=saveFile(request, user);
		if(bool){

			String e1[] = req.getParameterValues("e1");
			String e2[] = req.getParameterValues("e2");
			String e3[] = req.getParameterValues("e3");
			String w1[]=req.getParameterValues("w1");
			String w2[]=req.getParameterValues("w2");
			String w3[]=req.getParameterValues("w3");


			String es[]=new String[3];
			String ws[]=new String [3];
			for(int i=0;i<4;i++){
				es[0]=es[0]+e1[i]+"&";
				es[1]=es[1]+e2[i]+"&";
				es[2]=es[2]+e3[i]+"&";
				ws[0]=ws[0]+w1[i]+"&";
				ws[1]=ws[1]+w2[i]+"&";
				ws[2]=ws[2]+w3[i]+"&";
			}
			resume .setGraduateSchool(es[0]);
			resume .setEducatinBackground(es[1] );
			resume .setMajor(es[2]);
			resume .setCompany(ws[0]);
			resume .setWorkTime(ws[1] );
			resume .setMajor(ws[2]);

			if(user!=null){
				System.out.println(user.getId());
				User u = userSever.getById(user.getId());
				resume.setUser(u);
				u.setResume(resume);

				userSever.updata(u);;
				return "redirect:/client/personalCenter";
			}else{
				return "redirect:/client/resume";
			}
		}else {
			return "client/resume";
		}
	}

	/**
	 * 保存简历文件和保存头像
	 * @param request
	 * @param user
	 * @return
	 */


	private boolean saveFile(HttpServletRequest request, User user) {

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;	
		MultipartFile file = multipartRequest.getFile("pic");
		MultipartFile file1 = multipartRequest.getFile("resumeFile");
		String path = FilePath.userFilePath+user.getId()+"/";
		new File(path).mkdirs();
		if(file!=null && !"".equals(file.getOriginalFilename() )){
			String  a=file.getOriginalFilename();
			String [] s=new String[]{"bmp","jpg","gif","png"};
			for(int i=0;i<s.length;i++){
				if(!a.endsWith(s[i])){
					return false;
				}
			}
			try {
				Files.copy(file.getInputStream(), Paths.get( path+file.getOriginalFilename() )  );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(file1!=null && !"".equals(file1.getOriginalFilename() )){

			String  a=file1.getOriginalFilename();		
			String [] s=new String[]{"doc","docx","pdf","jpg"}; 
			for(int i=0;i<s.length;i++){
				if(!a.endsWith(s[i])){
					return false;
				}
			}
			try {
				Files.copy(file1.getInputStream(), Paths.get( path+file1.getOriginalFilename() )  );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}