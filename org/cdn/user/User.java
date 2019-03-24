/**
 * 用户请求信息
 */
package org.cdn.user;

/**
 * @author XianCheng
 *
 */
public class User {

	private String IP;
	private String URL;
	private String GROUP;
	
	public User(String ip,String url, String group)
	{
		this.GROUP=group;
		this.URL=url;
		this.IP=ip;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getGROUP() {
		return GROUP;
	}

	public void setGROUP(String gROUP) {
		GROUP = gROUP;
	}
	
	public String toString()
	{
		return "IP:"+IP+" URL:"+URL+" GROUP:"+GROUP;
	}
	
}
