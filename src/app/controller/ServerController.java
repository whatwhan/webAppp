package app.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import app.entity.Lesson;
import app.entity.ScoreRecord;
import app.entity.StudentInfo;
import app.util.StudentUtil;

@Controller
public class ServerController {
	@RequestMapping("login")
	@ResponseBody
	public String login(HttpServletRequest request,HttpSession httpSession)throws Exception{
		//String userid =(String) request.getAttribute("userid");
		//String userpsw = (String) request.getAttribute("userpsw");
		StudentUtil studentUtil =new StudentUtil();
		//studentUtil.setUserid(userid);
		//studentUtil.setUserpsw(userpsw);
		if(studentUtil.login()){
			httpSession.setAttribute("student",studentUtil);
			return "ok";
		}else{
			return "fail";
		}
	}
	@RequestMapping("getLessons")
	@ResponseBody
	public Map<Integer, Map<Integer, List<Lesson>>> getLessons(HttpSession httpSession)throws Exception{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		return studentUtil.getLessons();
	}
	@RequestMapping("getStudentInfo")
	@ResponseBody
	public StudentInfo getStudentInfo(HttpSession httpSession)throws Exception{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		return studentUtil.getStudentInfo();
	}
	@RequestMapping("getScoreList")
	@ResponseBody
	public List<ScoreRecord> getScoreList(HttpSession httpSession)throws Exception{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		return studentUtil.getScoreList();
	}
	
	
}




