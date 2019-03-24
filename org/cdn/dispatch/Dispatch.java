/**
 * 实现调度功能
 */
package org.cdn.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cdn.service.ForDispatch;
import org.cdn.service.Service;
import org.cdn.service.ServiceComparator;
import org.cdn.user.User;

/**
 * @author XianCheng
 *
 */
public class Dispatch implements DispatchInterface {

	public static final int BY_CAPACTIYRATE = 1; //按照容量使用比例(负荷)调度
	public static final int BY_WEIGHT = 2; //按照设置的权重调度
	public static final int BY_REMAININGCAPACITY = 3; //按照剩余容量大小进行调度
	
	protected int  dispatchType = BY_CAPACTIYRATE;

	Comparator<? super Service> c = new ServiceComparator();
	
	protected List<Service> serverList = null;
	
	protected DispatchAssist DA = null;
	
	private int cl=0;//两种方法分类（1：按照精确权重调度；2：按照随机权重调度）
	

	private Map gropMap = null; //分组名--服务器列表；分组名_default---默认服务器列表；_default---兜底默认服务器列表

	public Dispatch(List<Service> serverList,int dispatchType,int cl) 
	{		
		this.serverList = serverList;
		
		if(cl==2)
		{
			buildDispatchList();//按权重新组建业务列表
			
			DA = new DispatchAssist(this.dispatchList,dispatchType);
		}
		else if(cl==1)
		{
			DA = new DispatchAssist(this.serverList,dispatchType);
			//DA.start(); //辅助计算 定期按照一定规则刷新节点情况，方便调度
		}
		
		this.cl = cl;
		
		
		this.gropMap=DA.getGroupMap();
	}
	
	@Override
	public int dispatch(User user) //调度入口
	{
		// TODO Auto-generated method stub
		if(cl==1)
			return _dispatch1(user);
		else if(cl==2)
			return _dispatch2(user);
		
		return -1;

	}
	public int _dispatch1(User user) //按照精确权重（容量可用率）调度
	{
		List<Service> availableList = (List) gropMap.get(user.getGROUP());//获取配置的分组可用列表		
		if(null !=  availableList && availableList.size()>0)
		{
			availableList.sort(c); //按照使用率由低到高排序
			for(int i=0;i<availableList.size();i++)
			{
				if(availableList.get(i).onLoad(user)>=0) return 1; //分组内节点可调度则直接返回
			}
			
			List<Service> availableList_father = getFatherList(availableList);//获取节点的父节点（比如地市节点的父节点）
			if(null !=availableList_father && availableList_father.size()>0)
			{
				availableList_father.sort(c);//按照使用率由低到高排序
				for(int i=0;i<availableList_father.size();i++)
				{
					if(availableList_father.get(i).onLoad()>=0) return 2; //分组节点的父节点可调度则直接返回
				}
			}
		}

		List<Service> grouDefaultList = (List) gropMap.get(user.getGROUP()+"_default"); //获取分组的默认节点列
		if(null!=grouDefaultList && grouDefaultList.size()>0 )
        {
			grouDefaultList.sort(c); //按照使用率由低到高排序
    		for(int i=0;i<grouDefaultList.size();i++)
    		{
    			if(grouDefaultList.get(i).onLoad()>=0) return 3; //分组配置的兜底节点可调度则直接返回
    		};
        }
		
        List<Service> defaultList = (List) gropMap.get("default");//获取兜底列表
        if(null!=defaultList && defaultList.size()>0)
        {
        	defaultList.sort(c); //按照使用率由低到高排序
    		for(int i=0;i<defaultList.size();i++)
    		{
    			if(defaultList.get(i).onLoad()>=0) return 4; //分组配置的兜底节点可调度则直接返回
    		};
        }
        
        System.out.println(user);
		return -1; //没有可用的调度列表
	}
	
	public int _dispatch2(User user)//按照随机权重（容量可用率）调度
	{
		List<Service> availableList = (List) gropMap.get(user.getGROUP());	//获取配置的分组可用列表	
		if(null !=  availableList && availableList.size()>0)
		{
			if(availableList.get(new Random().nextInt(availableList.size())).onLoad2(user)>=0) //按照权重概率随机调度（有缺陷，如果调度不成功是否需要重试两次）
			{
				return 1;
			}
			
			List<Service> availableList_father = getFatherList(availableList);//获取节点的父节点（比如地市节点的父节点）
			if(null !=availableList_father && availableList_father.size()>0)
			{
				if(availableList_father.get(new Random().nextInt(availableList_father.size())).onLoad2(user)>=0)//按照权重概率随机调度（有缺陷，如果调度不成功是否需要重试两次）
				{
					return 2;
				}
			}
		}

		List<Service> grouDefaultList = (List) gropMap.get(user.getGROUP()+"_default"); //获取分组的默认节点列
		if(null !=grouDefaultList && grouDefaultList.size()>0)
		{
			if(grouDefaultList.get(new Random().nextInt(grouDefaultList.size())).onLoad2(user)>=0)//按照权重概率随机调度（有缺陷，如果调度不成功是否需要重试两次）
			{
				return 2;
			}
		}
			
        List<Service> defaultList = (List) gropMap.get("default"); //获取兜底列表
        if(null!=defaultList)
        {
        	if(defaultList.get(new Random().nextInt(defaultList.size())).onLoad2(user)>=0)
			{
				return 3;
			}
        }
        
        System.out.println(user);//打印未调度成功的用户信息
        
		return -1; //没有可用的调度列表
	}
	
	private List getFatherList(List<Service> list)//获取父节点列表
	{
		List tempList = new ArrayList();
		
		Service tempS=null;
		for(int i=0;i<list.size();i++)
		{
			tempS= list.get(i).getFather();
			if(null!=tempS)
			tempList.add(tempS);
		}
		
		//tempList.  //列表去重
		
		return tempList;
	}
	
	public int dispatch1(User user)//按照权重调度（多参数权重情况下有缺陷）
	{
		// TODO Auto-generated method stub
		
		serverList.sort(c);
		for(int i=0;i<serverList.size();i++)
		{
			if(serverList.get(i).onLoad()>=0) break;
		}
		if(ForDispatch.getNumberOfZeroAvailableFactor()>=serverList.size())
		{
			for(int j=0;j<serverList.size();j++)
			{
				serverList.get(j).getForDispatch().setUseable();
			}
			
			ForDispatch.setNumberOfZeroAvailableFactor(0);
		}
		
		return 0;

	}
	
	private List<Service> dispatchList = new ArrayList<>();
	private int sum=0;
	public void buildDispatchList()//构建权重比例列表
	{
		
		for(int i=0;i<serverList.size();i++)
		{
			for(int j=0;j<serverList.get(i).getForDispatch().getWeightFactor();j++)
			{
				dispatchList.add(serverList.get(i));
			}
			sum+=serverList.get(i).getForDispatch().getWeightFactor();
		}
		Collections.shuffle(dispatchList);
		
	}
	
	public int dispatch2(User user)//按权重比例掉度
	{
		dispatchList.get(new Random().nextInt(sum)).onLoad2();
		return 0;
	}
	
	public void release()//释放调度辅助线程
	{
		DA.stop();
	}

}
