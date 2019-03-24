/**
 * 实现调度辅助功能（定时计算支撑调度）
 */
package org.cdn.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdn.service.ForDispatch;
import org.cdn.service.Service;
import org.cdn.service.ServiceComparator;

/**
 * @author XianCheng
 *
 */
public  class DispatchAssist implements DispathcAssistInterface<Service>,Runnable {

	protected List<Service> list = null; //备用调度列表，从列表中获取调度参数进行计算，按照一定的规则排序，返回排序后的可用调度列表
	protected int dispatchType=0;
	
	private Thread t;
	private boolean working = true;
	
	private Map gropMap = new HashMap();
	
	Comparator<? super Service> c = new ServiceComparator();
	
	public DispatchAssist(List<Service> list,int dispatchType)
	{
		this.list = list;
		this.dispatchType=dispatchType;
		ForDispatch.setDispatchType(dispatchType);
		init();
		caculate();
	}
	
	public void init()
	{

	}
	
	@Override
	public List<Service> doDispatchAssistent() //单次调用获得可用列表
	{
		// TODO Auto-generated method stub
		caculate();
		return list;
	}
	
	protected synchronized void caculate() //按照一定规则进行预处理（按照分组构建调度列表）
	{		
		//list.spliterator(c);		
		HashMap<String,List<Service>> tempGroupMap = new HashMap();
		for(int i = 0;i<list.size();i++)
		{
			List tempGroupList = list.get(i).getForDispatch().getGroupList();
			for(int j=0;j<tempGroupList.size();j++)
			{
				String tempGroup = (String) tempGroupList.get(j);
				if(tempGroupMap.containsKey(tempGroup))
				{
					tempGroupMap.get(tempGroup).add(list.get(i));
				}
				else
				{
					List<Service> tempList = new ArrayList();
					tempList.add(list.get(i));
					tempGroupMap.put(tempGroup, tempList);
				}
			}
		}
		this.gropMap = tempGroupMap;
		
	}
	
	public Map getGroupMap()//获取处理后的 分组--服务列表 map表
	{
		return this.gropMap;
	}
	
	@Override
	public void run() //线程后台定时执行
	{
		// TODO Auto-generated method stub
		
		while(true)
		{
			if(!working)
				break;
			
			try {
				//System.out.println("caculate");
				caculate();
				Thread.sleep(10);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}		
	}
	
	public void start()//启动后台线程
	{
		// TODO Auto-generated method stub
		System.out.println("Starting dispatch assitent" );
			      if (t == null) {
			         t = new Thread (this,"dispatchAssitent");
			         t.start();
			      }
	}
	
	public void stop()//停止后台线程
	{
		// TODO Auto-generated method stub
				System.out.println("Stop dispatch assitent" );
			    this.working=false;
	}
}
