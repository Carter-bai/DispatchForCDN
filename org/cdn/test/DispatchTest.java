/**
 * 测试验证类
 */
package org.cdn.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdn.dispatch.Dispatch;
import org.cdn.service.Service;
import org.cdn.user.User;

/**
 * @author XianCheng
 *
 */
public class DispatchTest {
	public static void main(String[] args) {
		
		//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		// TODO Auto-generated method stub
		List<Service> list = new ArrayList<Service>();
		Service s=null;

		List<Map> ipList = null;
		List<String> urlList = null;
		List<String> groupList = null;
		HashMap ipMap =null;
		
		ipList = new ArrayList();
		ipMap = new HashMap();
		ipMap.put("FromIP", "5.5.5.0");
		ipMap.put("ToIP", "5.5.5.255");
		ipList.add(ipMap);
		
		urlList = new ArrayList();
		urlList.add("http://www.cdn.0");
		urlList.add("http://www.cdn.1");
		urlList.add("http://www.cdn.3");
		
		groupList = new ArrayList();
		groupList.add("group0");
		groupList.add("group1");
		groupList.add("group2");
		groupList.add("group3");
		groupList.add("group4");
		groupList.add("group5");
		
		for(int i=0;i<10;i++)
		{
			s= new Service("Service "+i);
			s.getForDispatch().setWeightFactor(i%100+1);
			s.getForDispatch().setCapactity(1000000);
			s.getForDispatch().setIpList(ipList);
			s.getForDispatch().setUrlList(urlList);
			s.getForDispatch().setGroupList(groupList);
			list.add(s);
		}
		
		/*
		ipList = new ArrayList();
		ipMap = new HashMap();
		ipMap.put("FromIP", "5.5.5.51");
		ipMap.put("ToIP", "5.5.5.100");
		ipList.add(ipMap);
		
		urlList = new ArrayList();
		urlList.add("http://www.cdn.4");
		urlList.add("http://www.cdn.5");
		urlList.add("http://www.cdn.6");
		
		groupList = new ArrayList();
		groupList.add("group2");
		groupList.add("group3");
		
		for(int i=11;i<20;i++)
		{
			s= new Service("Service "+i);
			s.getForDispatch().setWeightFactor(i%10+1);
			s.getForDispatch().setCapactity(1000000);
			s.getForDispatch().setIpList(ipList);
			s.getForDispatch().setUrlList(urlList);
			s.getForDispatch().setGroupList(groupList);
			list.add(s);
		}
		
		ipList = new ArrayList();
		ipMap = new HashMap();
		ipMap.put("FromIP", "5.5.5.80");
		ipMap.put("ToIP", "5.5.5.255");
		ipList.add(ipMap);
		
		urlList = new ArrayList();
		urlList.add("http://www.cdn.5");
		urlList.add("http://www.cdn.6");
		urlList.add("http://www.cdn.7");
		urlList.add("http://www.cdn.8");
		urlList.add("http://www.cdn.9");
		urlList.add("http://www.cdn.10");
		
		groupList = new ArrayList();
		groupList.add("group2");
		groupList.add("group3");
		groupList.add("group4");
		groupList.add("group5");
		
		for(int i=21;i<50;i++)
		{
			s= new Service("Service "+i);
			s.getForDispatch().setWeightFactor(i%10+1);
			s.getForDispatch().setCapactity(1000000);
			s.getForDispatch().setIpList(ipList);
			s.getForDispatch().setUrlList(urlList);
			s.getForDispatch().setGroupList(groupList);
			list.add(s);
		}
		
		*/
		
		User u =null;
		
		Dispatch dispatch = new Dispatch(list,2,1);

		System.out.println(System.currentTimeMillis());
		for(int i=0;i<10000000;i++)
	                	{
			if(i==0)
				System.out.println("begin");
			else if(i % 100 ==0)
			{
				//System.out.println(i);
				for(int j=0;j<list.size();j++)
				{
					//System.out.println(list.get(j).toString());
				}
			}	
			u=new User("5.5.5."+i%255,"http://www.cdn."+i%10,"group"+i%5);
			dispatch.dispatch(u);
			/*
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}*/
		}
		
		for(int j=0;j<list.size();j++)
		{
			System.out.println(list.get(j).toString());
		}
		System.out.println("end");
		
		System.out.println(System.currentTimeMillis());
		dispatch.release();
	}

}
