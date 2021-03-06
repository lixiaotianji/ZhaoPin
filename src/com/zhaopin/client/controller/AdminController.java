package com.zhaopin.client.controller;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zhaopin.client.server.AdminServer;
import com.zhaopin.po.Admin;
import com.zhaopin.po.Privilege;

@Controller
@RequestMapping("/client")
public class AdminController {
	
	//注入admin服务	adminServerImpl
	@Resource(name="adminServerImpl")
	private AdminServer adminServer;
	
	/**
	 * 显示登陆页面
	 * @return
	 */
	@RequestMapping("/loginAdmin")
	public String loginAdmin(HttpSession session){
		//直接返回  前缀加 字符串+jsp的页面
		if(session.getAttribute("admin")!=null){
			//如果管理员已经登陆
			return "redirect:/admin/position";
		}
		return "client/loginAdmin";
	}
	
	/**
	 * 验证用户名密码是否正确
	 * @param admin
	 * @return
	 */
	@RequestMapping("/loginAdmin/login")
	public String login(@ModelAttribute Admin admin,HttpSession session, @RequestParam String valifCode,Model model){
		
		//如果输入的验证码不正确
		if( !valifCode.equals(session.getAttribute("valifCode") ))  {
			model.addAttribute("error","验证码错误");
			return "client/loginAdmin";
		}
		Admin a =adminServer.login(admin);
		System.out.println("管理员登陆");
		//登陆成功
		if(a!=null){
			Set<Privilege> privileges =  a.getPrivileges();
			Integer [] ids = new Integer[privileges.size()+1];
			if(privileges!=null){
				Arrays.fill(ids, 0);
				int index = 0 ;
				for(Privilege p : privileges){
					ids[index++]=p.getId();
				}
			}
			session.setAttribute("admin", a);
			return "redirect:/admin/position";
		}
		//登陆失败
		model.addAttribute("error","账号或密码不正确");
		return "client/loginAdmin";
		
	}

	public AdminServer getAdminServer() {
		return adminServer;
	}

	public void setAdminServer(AdminServer adminServer) {
		this.adminServer = adminServer;
	}
	
	
}
