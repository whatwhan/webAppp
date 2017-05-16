package app.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.entity.Lesson;
import app.entity.ScoreRecord;
import app.entity.StudentInfo;



/**
 * HTML 解析工具类
 * 
 * @author Jayin Ton
 * 
 */
public class HtmlParser {

	/**
	 * 解析课程html Note: 同一节课，会有>=1个课程 为此用List<Lesson>来存贮
	 * 
	 * @param html
	 *            课程表html
	 * @param map
	 *            周1-7，课时1-5
	 * @return map[周数，对应当日的课程列表]
	 */
	public static Map<Integer, Map<Integer, List<Lesson>>> parseHtmlForLesson(
			String html, Map<Integer, Map<Integer, List<Lesson>>> map) {
		for (int i = 1; i <= 7; i++) {
			HashMap<Integer, List<Lesson>> hashmap = new HashMap<Integer, List<Lesson>>();
			for (int j = 1; j <= 7; j++)
				hashmap.put(j, new ArrayList<Lesson>());
			map.put(i, hashmap);
		}
		Pattern p = Pattern.compile("<td valign=top align=center>(.*)</td>");
		Matcher m = p.matcher(html);
		int day = 1, lessonTime = 1;
		while (m.find()) {
			List<Lesson> mlist = new ArrayList<Lesson>();
			String s = m.group(1).trim();
			s = s.replaceAll("<br>", "&nbsp;");
			String[] ms = s.split("&nbsp;");
			int j = 0;
			int count = 0;
			Lesson l = null;
			while (j < ms.length) {
				while (ms[j].equals(""))
					j++;
				switch (count) {
				case 0:
					l = new Lesson();
					l.LessonName = ms[j++].trim();
					count++;
					break;

				case 1:
					l.Time = ms[j++].trim();
					count++;
					break;
				case 2:
					l.address = ms[j++].trim();
					count++;
					break;

				case 3:
					l.Teacher = ms[j++].trim();
					mlist.add(l);
					count = 0;
					System.out.println(l.toString());
					break;
				}
			}
			if (ms.length == 0) {
				l = new Lesson();
				mlist.add(l);
			}
			(map.get(day)).put(lessonTime, mlist);
			if (day + 1 > 7) {
				day = 1;
				lessonTime++;
			} else {
				day++;
			}
		}
		return map;
	}

	/**
	 * 解析成绩列表
	 * 
	 * @param html
	 *            成绩页页代码
	 * @param list
	 *            成绩列表
	 * @return 成绩列表
	 */
	public static List<ScoreRecord> parseHtmlForScore(String html,
			List<ScoreRecord> list) {

		String regex = "<p class=MsoNormal align=center style='text-align:center'>([0-9a-zA-Z]+)</p>"; // 课程代码
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		while (m.find()) {
			int count = m.groupCount();
			for (int i = 1; i <= count; i++) {
				ScoreRecord sr = new ScoreRecord();
				sr.lessonCode = m.group(i);
				list.add(sr);
			}
		}

		regex = "<p class=MsoNormal><span style='font-family:宋体'>(.*)</span></p>"; // 课程名称
		p = Pattern.compile(regex);
		m = p.matcher(html);
		int i = 0;
		while (m.find()) {

			list.get(i++).lessonName = m.group(1);
			if (i == list.size())
				break;
		}
		regex  ="<p class=MsoNormal align=center style='text-align:center'><span\\s*\\r*style='font-family:宋体'>(.*)</span></p>";// 课程类别（选修or必修or通识课or公选课）
		p = Pattern.compile(regex);
		m = p.matcher(html);
		i = 0;
		while (m.find()) {
		    if(m.group(1).equals("必修")||m.group(1).equals("选修")||m.group(1).equals("通识课")||m.group(1).equals("公选课")||m.group(1).equals("必修")){
		    	list.get(i++).category = m.group(1);
				if (i == list.size())
					break;
		    }
		}
		regex = "<p class=MsoNormal align=center style='text-align:center'><span lang=EN-US>(.*)</span></p>";// 第一个为学分，第二个为成绩
		p = Pattern.compile(regex);
		m = p.matcher(html);
		i = 0;
		boolean sy = true;
		while (m.find()) {
			if (sy) {
				list.get(i).lessonScore = m.group(1);
			} else {
				list.get(i).score = m.group(1);
				i++;
			}
			sy = !sy;
			if (i == list.size())
				break;
		}

		return list;
	}
	/**
	 * 解析学生个人信息
	 * 
	 * @param html  
	 *            html code
	 * @param stu
	 *            a student
	 * @return the information of the student
	 */
	public static StudentInfo parseHtmlForStudentInfo(String html,
			StudentInfo stu) {
		String regex = "<td height=\"26\" width=\"100\" align=\"center\">(.*)</td>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);
		List<String> list = new ArrayList<String>();
		while (m.find()) {
			int count = m.groupCount();
			for (int i = 1; i <= count; i++) {
				if (m.group(i).trim().equals("&nbsp;"))
					list.add("null");
				else
					list.add(m.group(i).trim());
			}
		}

		regex = "<td height=\"26\" colspan=\"3\" align=\"center\">(.*)</td>";
		p = Pattern.compile(regex);
		m = p.matcher(html);
		while (m.find()) {
			int count = m.groupCount();
			for (int i = 1; i <= count; i++) {
				if (m.group(i).trim().equals("&nbsp;"))
					list.add("null");
				else
					list.add(m.group(i).trim());
			}
		}
		stu.id = list.get(0);
		stu.neme = list.get(1);
		stu.nickName = list.get(2);
		stu.sex = list.get(3);
		stu.birthday = list.get(4);
		stu.nation = list.get(5);
		stu.political = list.get(6);
		stu.classNumber = list.get(7);
		stu.department = list.get(8);
		stu.edu_system = list.get(9);
		stu.enter_time = list.get(10);
		stu.edu_status = list.get(11);
		stu.contact = list.get(14);
		stu.homeTown = list.get(17);
		stu.major = list.get(18);
		stu.system = list.get(19);
		stu.identify = list.get(20);
		stu.address = list.get(21);
		return stu;
	}

}
