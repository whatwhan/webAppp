package app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import app.entity.Lesson;
import app.entity.ScoreRecord;
import app.entity.StudentInfo;



public class StudentUtil {
	private   String userid = "3114002595";
	private   String userpsw = "573039";
	private   CookieStore mCookieStore = null;
	private   String randomNumber = "123456";
	private   String ASPSESSIONIDAQCCRCDT="";



	public   boolean login() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter("http.socket.timeout", 10000);
		HttpGet get = new HttpGet("http://jwc.wyu.edu.cn/student/rndnum.asp"); // get																			// rando																				// co		
		HttpPost post = new HttpPost("http://jwc.wyu.edu.cn/student/logon.asp");// login
		HttpResponse response = null;	
		response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Header h = response.getFirstHeader("Set-Cookie");		
			HeaderElement[] he = h.getElements();
		 	randomNumber = he[0].getValue();
			mCookieStore = client.getCookieStore();
			System.out.println(randomNumber);
			// login
			String UserCode = userid;
			String UserPwd = userpsw;
			String Validate = randomNumber;
			String Submit = "%CC%E1+%BD%BB";
			// 构建表头
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("UserCode", UserCode));
			parameters.add(new BasicNameValuePair("UserPwd", UserPwd));
			parameters.add(new BasicNameValuePair("Validate", Validate));
			parameters.add(new BasicNameValuePair("Submit", Submit));
			HttpEntity entity1 = new UrlEncodedFormEntity(parameters, "utf-8");
			post.setEntity(entity1);
			//	post.
			post.setHeader("Host", "jwc.wyu.edu.cn"); 	
			post.setHeader("Referer", "http://jwc.wyu.edu.cn/student/body.htm"); // 12.9
			ASPSESSIONIDAQCCRCDT=response.getAllHeaders()[7].toString().split(" ")[1].split("=")[1];
	        List<Cookie> cookies = mCookieStore.getCookies();
	        StringBuffer stringBuffer = new StringBuffer();
	        stringBuffer.append("NGID=dfd63e2a-dc25-e3bb-cccd-a712cc970fd6; dfd63e2a-dc25-e3bb-cccd-a712cc970fd6=http%3A//jwc.wyu.edu.cn/student/body.htm; ");
			
	        for (Cookie c : cookies) {
	            stringBuffer.append(c.getName() +"="+c.getValue()+ "; ");
	        }
	        post.setHeader("Cookie",stringBuffer.toString());
			System.out.println(stringBuffer.toString());
				
			// 学校坑了！
			client.getConnectionManager().shutdown();
			// 设置httpClient参数，不自动重定向
			HttpParams httpParams = new BasicHttpParams();
			HttpClientParams.setRedirecting(httpParams, false);
			client = new DefaultHttpClient(httpParams);
			
			client.setCookieStore(mCookieStore);
			

			response = client.execute(post);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("login successfully!");
				//System.out.println(response.getAllHeaders()[3]);
				System.out.println(EntityUtils.toString(response.getEntity(), "gbk"));
				//Header header = post.getResponseHeader("location");
				client.getConnectionManager().shutdown();
				//System.out.println(getScoreList());
				//System.out.println(getLessons());
				//System.out.println(getStudentInfo());
				return true;

			} else {
				client.getConnectionManager().shutdown();
				System.out.println("code:--" + response.getStatusLine().getStatusCode());
				System.out.println(EntityUtils.toString(response.getEntity(), "gbk"));
				System.out.println("login faild!");
				return false;
			}

		} else {
			client.getConnectionManager().shutdown();
			System.out.println("get random code faild!");
			return false;
		}
	}

	public   Map<String,List<ScoreRecord>> getScoreList() throws ClientProtocolException, IOException {
		
		DefaultHttpClient client = new DefaultHttpClient();
		DefaultHttpClient client1 = new DefaultHttpClient();
		DefaultHttpClient client2 = new DefaultHttpClient();

		client.setCookieStore(mCookieStore);
		client1.setCookieStore(mCookieStore);
		client2.setCookieStore(mCookieStore);

		HttpResponse response = null;
		response = client1.execute(new HttpGet("http://jwc.wyu.edu.cn/student/createsession_a.asp"));
		response = client2.execute(new HttpGet("http://jwc.wyu.edu.cn/student/createsession_b.asp"));
		client1.getConnectionManager().shutdown();
		client2.getConnectionManager().shutdown();
		HttpGet get = new HttpGet("http://jwc.wyu.edu.cn/student/f4_myscore.asp");
		get.setHeader("Referer", "http://jwc.wyu.edu.cn/student/menu.asp");// 12.9发现学校够坑的！
		get.setHeader("Cookie", "NGID=be06d5d6-9e2e-deb2-3f65-ea6113f26de2; be06d5d6-9e2e-deb2-3f65-ea6113f26de2=http%3A//jwc.wyu.edu.cn/student/body.htm; ASPSESSIONIDAQCCRCDT="+ASPSESSIONIDAQCCRCDT+"; LogonNumber="+randomNumber);
		response = client.execute(get);
		// response = client.execute(new HttpGet(
		// "http://jwc.wyu.edu.cn/student/f4_myscore.asp"));
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String html = EntityUtils.toString(response.getEntity(), "gb2312");
			client.getConnectionManager().shutdown();
			//System.out.println("huiui"+html);
			return HtmlParser.parseHtmlForScore(html);
		}

		client.getConnectionManager().shutdown();
		return null;
	}
	
	public   List<ScoreRecord> parseHtmlForScore(String html,
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
	 * 获取课表
	 * 
	 * @return 课表列表
	 * @throws ETipsException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public   Map<String, Map<String, List<Lesson>>> getLessons()
			throws Exception, ClientProtocolException, IOException {
		Map<String, Map<String, List<Lesson>>> map = new HashMap<String, Map<String, List<Lesson>>>();
		if (mCookieStore == null)
			throw new Exception("you should login before call this method");

		DefaultHttpClient client = new DefaultHttpClient();
		client.setCookieStore(mCookieStore);
		HttpGet get = new HttpGet("http://jwc.wyu.edu.cn/student/f3.asp");
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String html = EntityUtils.toString(response.getEntity(), "gb2312");
			client.getConnectionManager().shutdown();
			return HtmlParser.parseHtmlForLesson(html, map);
		}
		client.getConnectionManager().shutdown();
		return null;
	}
	/**
	 * 获取学生信息
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public   StudentInfo getStudentInfo() throws ClientProtocolException,
			IOException {
		StudentInfo stu = new StudentInfo();
		DefaultHttpClient client = new DefaultHttpClient();
		client.setCookieStore(mCookieStore);
		HttpResponse response = client.execute(new HttpGet(
				"http://jwc.wyu.edu.cn/student/f1.asp"));
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String html = EntityUtils.toString(response.getEntity(), "gb2312");
			return HtmlParser.parseHtmlForStudentInfo(html, stu);
		} else {
			System.out
					.println("Get information faild .Check you Network envirenment pleased");
		}
		return stu;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserpsw() {
		return userpsw;
	}

	public void setUserpsw(String userpsw) {
		this.userpsw = userpsw;
	}

	public CookieStore getmCookieStore() {
		return mCookieStore;
	}

	public void setmCookieStore(CookieStore mCookieStore) {
		this.mCookieStore = mCookieStore;
	}

	public String getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(String randomNumber) {
		this.randomNumber = randomNumber;
	}

	public String getASPSESSIONIDAQCCRCDT() {
		return ASPSESSIONIDAQCCRCDT;
	}

	public void setASPSESSIONIDAQCCRCDT(String aSPSESSIONIDAQCCRCDT) {
		ASPSESSIONIDAQCCRCDT = aSPSESSIONIDAQCCRCDT;
	}
}