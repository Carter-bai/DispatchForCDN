/**
 * 实现业务部件能力
 */
package org.cdn.service;

import org.cdn.user.User;

/**
 * @author XianCheng
 *
 */
public class Service {

	private String name=null;
	private ForDispatch fd = new ForDispatch(); //调度辅助类
	
	Service father = null; //父业务节点
	
	public Service(String name)
	{
		this.name = name;
		init();
	}
	
	private void init()//初始化
	{
		fd.setActive(true);
	}
	
	public int onLoad() //业务加载
	{
		int result = fd.canLoad();
		if(result>=0)
		{
			//进行实际业务加载
		}
		return result;
	}
	
	public int onLoad(User user) //业务加载
	{
		int result = fd.canLoad(user);
		if(result>=0)
		{
			//进行实际业务加载
		}
		return result;
	}
	
	public int onLoad2() //业务加载
	{
		int result = fd.canLoad2();
		if(result>=0)
		{
			//进行实际业务加载
		}
		return result;
	}
	
	public int onLoad2(User user) //业务加载
	{
		int result = fd.canLoad2(user);
		if(result>=0)
		{
			//进行实际业务加载
		}
		return result;
	}
	
	public ForDispatch getForDispatch()
	{
		return this.fd;
	}
	
	public Service getFather()
	{
		return father;
	}
	
	public String toString()
	{
		return this.name + fd.toString();
	}
	
}
