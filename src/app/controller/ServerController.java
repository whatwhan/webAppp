package app.controller;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import app.entity.Lesson;
import app.entity.ScoreRecord;
import app.entity.StudentInfo;
import app.util.HtmlParser;
import app.util.StudentUtil;
import app.entity.Xueqichengji;
@Controller
public class ServerController {
	@Resource
	JdbcTemplate jdbcTemplate;
	DecimalFormat df = new DecimalFormat("#.00");
	@RequestMapping("login")
	@ResponseBody
	public Object login(HttpServletRequest request,HttpSession httpSession)throws Exception{
		String userid =(String) request.getParameter("userid");
		String userpsw = (String) request.getParameter("userpsw");
		StudentUtil studentUtil =new StudentUtil();
		if(userid!=null&&userpsw!=null){
			studentUtil.setUserid(userid);
			//System.out.println("userid:"+userid);
			studentUtil.setUserpsw(userpsw);
		}
		if(studentUtil.login()){
			httpSession.setAttribute("student",studentUtil);
			return true;
		}else{
			return false;
		}
	}
	@RequestMapping("getLessons")
	@ResponseBody
	public Object getLessons(HttpSession httpSession,HttpServletRequest request)throws Exception{
		try{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		String mark =(String) request.getParameter("mark");
		if(mark==null||Integer.parseInt(mark)==0){
			
			String kebiaostr=jdbcTemplate.queryForObject("select text from kebiao where id='"+studentUtil.getUserid()+"'", String.class);
			
			if(kebiaostr==null){
				return false;
			}
			return  JSONObject.fromObject(kebiaostr);
		}else if(Integer.parseInt(mark)==1){
			
			Map<String, Map<String, List<Lesson>>> maps=studentUtil.getLessons();
			String kebiaostr=JSONObject.fromObject(maps).toString();
			jdbcTemplate.update("delete from kebiao where id="+studentUtil.getUserid());
			jdbcTemplate.update("insert into kebiao (id,text) values (?,?)", new Object[]{studentUtil.getUserid(),kebiaostr});
			
			return maps;
		}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	@RequestMapping("getStudentInfo")
	@ResponseBody
	public Object getStudentInfo(HttpSession httpSession)throws Exception{
		try{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		
		return studentUtil.getStudentInfo();
		}catch(Exception e){
			return false;
		}
	}

	@RequestMapping("getJidian")
	@ResponseBody
	public Object getJidian(HttpSession httpSession,HttpServletRequest request)throws Exception{
		try{
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		if(studentUtil==null){
			return false;
		}
		String mark =(String) request.getParameter("mark");
		String marks [] =mark.split(",");
		String mapstr=jdbcTemplate.queryForObject("select text from jidian where id='"+studentUtil.getUserid()+"'", String.class);
		JSONObject jsonObject=JSONObject.fromObject(mapstr);
		double xuefen=0;
		double jidian=0;
		for(int i=0;i<marks.length;i++){
			System.out.println("进来");
			int num=0;
			try{
				//System.out.println("asd"+marks[i]);
				num=Integer.parseInt(marks[i]);
				xuefen += ((JSONObject) jsonObject.get(num+"")).getDouble("xuefen");
				jidian += ((JSONObject) jsonObject.get(num+"")).getDouble("jidian");
			}catch(Exception e){e.printStackTrace();}
		}
		System.err.println(jidian+" "+xuefen);
		
		return df.format(jidian/xuefen) ;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping("getScoreList")
	@ResponseBody
	public Object getScoreList(HttpSession httpSession,HttpServletRequest request,HttpServletResponse httpServletResponse)throws Exception{
		
		StudentUtil studentUtil = (StudentUtil) httpSession.getAttribute("student");
		request.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("text/json;charset=UTF-8");  
		if(studentUtil==null){
			return false;
		}
		try{
			String mark =request.getParameter("mark");
			if(mark==null||Integer.parseInt(mark)==0){
				
				
				try {
					String mapstr=jdbcTemplate.queryForObject("select text from chengji where id='"+studentUtil.getUserid()+"'", String.class);
					if(mapstr!=null&&(!mapstr.equals(""))){
						System.out.println(mapstr);
						return JSONObject.fromObject(mapstr);
					}else{
						Map<String,List<ScoreRecord>> maps= studentUtil.getScoreList();
						if(maps==null){
							return false;
						}
						String cehngjistr=JSONObject.fromObject(maps).toString();
						String jidianstr=JSONArray.fromObject(HtmlParser.chajidian(maps)).toString();
						jdbcTemplate.update("delete from chengji where id="+studentUtil.getUserid());
						jdbcTemplate.update("delete from jidian where id="+studentUtil.getUserid());
						jdbcTemplate.update("insert into chengji (id,text) values (?,?)", new Object[]{studentUtil.getUserid(),cehngjistr});
						jdbcTemplate.update("insert into jidian (id,text) values (?,?)", new Object[]{studentUtil.getUserid(),jidianstr.subSequence(1,jidianstr.length()-1)});
						return maps;
					}
				} catch (Exception e) {
					return false;
				}
				
				
			}else if(Integer.parseInt(mark)==1){
				
				
			Map<String,List<ScoreRecord>> maps= studentUtil.getScoreList();
			if(maps==null){
				return false;
			}
			String cehngjistr=JSONObject.fromObject(maps).toString();
			String jidianstr=JSONArray.fromObject(HtmlParser.chajidian(maps)).toString();
			jdbcTemplate.update("delete from chengji where id="+studentUtil.getUserid());
			jdbcTemplate.update("delete from jidian where id="+studentUtil.getUserid());
			jdbcTemplate.update("insert into chengji (id,text) values (?,?)", new Object[]{studentUtil.getUserid(),cehngjistr});
			jdbcTemplate.update("insert into jidian (id,text) values (?,?)", new Object[]{studentUtil.getUserid(),jidianstr.subSequence(1,jidianstr.length()-1)});
			return maps;
			
			
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
}




