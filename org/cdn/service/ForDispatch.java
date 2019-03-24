/**
 * 实现根据业务情况进行调度辅助处理（判断是否可以调度）
 */
package org.cdn.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.cdn.dispatch.Dispatch;
import org.cdn.user.User;

/**
 * @author XianCheng
 *
 */
public class ForDispatch {
	
	//固定参数
	protected long capactity=-1; //业务部件的容量
	protected int weightFactor = 5;//权重 1~100
	protected List<Map> ipList;//可服务IP列表
	protected List<String> urlList;//可服务的URL列表
	protected List<String> groupList;//所属的分组列表
	protected static int dispatchType=0;//调度方式 1：容量比例；2：权重；	
	
	//动态参数
	protected boolean isActive=false;//当前业务是否可用	
	protected float capacityUsageRate=0; //负载比例 0~100
	protected float cpuUsageRate=0; //CPU负载比例 0~100
	protected float memUsageRate=0; //MEM负载比例 0~100
	protected float netUsageRate=0; //网络负载比例 0~100		
	protected long currentDuty=0; //当前承载的业务量
	protected long historyDuty=0; //历史总负载业务量
	protected int availableWeightFactor=5;//可用权重
	protected float availableFactor=100;//可用因子（根据设置选项计算出具体的可用因子）
	protected static int numberOfZeroAvailableFactor = 0; //可用因子为0的个数
	
	

	public void setUnUseable() //设置为不可调度
	{
		availableWeightFactor=0;
		availableFactor=0;
		numberOfZeroAvailableFactor++;
	}
	
	public void setUseable() //设置为可以调度并恢复动态参数为初始值
	{
		availableWeightFactor=weightFactor;
		availableFactor=100;
	}
	
	public int canLoad(User user)//判断是否可支持业务加载
	{		
		if(!isActive)//设备未激活
		{
			return -1;
		}
		else if(currentDuty >= capactity)//设备已满载
		{
			setUnUseable();
			return -2;
		}
		
		//判断IP是否在此范围内
		int i=0;
		for(;i<ipList.size();i++)
		{
		if(inRang((String) ipList.get(i).get("FromIP"),(String) ipList.get(i).get("ToIP"),user.getIP()))
			{
				break;
			}
		}
		if(i>=ipList.size())
		{
			return -4;
		}
				
	
		this.currentDuty++;
		this.historyDuty++;
		this.capacityUsageRate = (float)this.currentDuty/capactity*100;
		availableFactor = 100-capacityUsageRate;
		
		return 0;
	}
	
	private boolean inRang(String beginIP, String endIp, String compareIP)//判读IP是否在可用范围内
	{
		boolean r= false;
		try {
			if(ipToLong(InetAddress.getByName(compareIP))>=ipToLong(InetAddress.getByName(beginIP)) 
					&& ipToLong(InetAddress.getByName(compareIP))<=ipToLong(InetAddress.getByName(endIp)))
			{
				r=true;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return r;
	}
	
	private long ipToLong(InetAddress ip)//IP地址转换为长整型数支持比较
	{
		long result =0;
		byte[] ipadds= ip.getAddress();
		
		for(byte b:ipadds)
		{
			result <<= 8;
			result|=b&0xff;			
		}
		
		return result;
	}
	
	public int canLoad()//判断是否可支持业务加载
	{
		caculateFactor();
		
		if(!isActive)//设备未激活
		{
			return -1;
		}
		else if(currentDuty >= capactity)//设备已满载
		{
			setUnUseable();
			return -2;
		}
		else if(availableFactor<=0)//可用因子不足
		{
			setUnUseable();
			return -3;
		}
	
		this.currentDuty++;
		this.historyDuty++;
		this.capacityUsageRate = (float)this.currentDuty/capactity;
				
		if(dispatchType == Dispatch.BY_WEIGHT || dispatchType == Dispatch.BY_REMAININGCAPACITY)//权重模式需要减掉可用因子
		{
			availableWeightFactor--;
			if(availableWeightFactor<=0)
			{
				setUnUseable();
			}
		}
		
		
		return 0;
	}
	
	public int canLoad2()//判断是否可支持业务加载
	{
		caculateFactor();
		
		if(!isActive)//设备未激活
		{
			return -1;
		}
		else if(currentDuty >= capactity)//设备已满载
		{
			setUnUseable();
			return -2;
		}
	
		this.currentDuty++;
		this.historyDuty++;
		this.capacityUsageRate = (float)this.currentDuty/capactity;	
		
		return 0;
	}
	
	public int canLoad2(User user)//判断是否可支持业务加载
	{		
		return canLoad(user);
	}
	
	
	public void caculateFactor()//计算可用因子，可以根据实际情况调整
	{
		//availableFactor = availableWeightFactor*(1-Math.max(capacityUsageRate, cpuUsageRate)/100);
		//availableFactor = availableWeightFactor;
	}
	
	
	
	public long getCapactity() {
		return capactity;
	}
	public void setCapactity(long capactity) {
		this.capactity = capactity;
	}
	public int getWeightFactor() {
		return weightFactor;
	}
	public void setWeightFactor(int weightFactor) {
		this.weightFactor = weightFactor;
		//this.availableWeightFactor = weightFactor;
		//caculateFactor();
	}
	public List<Map> getIpList() {
		return ipList;
	}
	public void setIpList(List<Map> ipList) {
		this.ipList = ipList;
	}
	public List<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	public List<String> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		if(isActive)
		{
			setUseable();
		}
		else
			numberOfZeroAvailableFactor++;
		
	}
	public float getCapacityUsageRate() {
		return capacityUsageRate;
	}

	public float getCpuUsageRate() {
		return cpuUsageRate;
	}
	public void setCpuUsageRate(float cpuUsageRate) {
		this.cpuUsageRate = cpuUsageRate;
	}
	public float getMemUsageRate() {
		return memUsageRate;
	}
	public void setMemUsageRate(float memUsageRate) {
		this.memUsageRate = memUsageRate;
	}
	public float getNetUsageRate() {
		return netUsageRate;
	}
	public void setNetUsageRate(float netUsageRate) {
		this.netUsageRate = netUsageRate;
	}
	public static int getDispatchType() {
		return dispatchType;
	}
	public static void setDispatchType(int dispatchType) {
		ForDispatch.dispatchType = dispatchType;
	}
	public long getCurrentDuty() {
		return currentDuty;
	}

	public long getHistoryDuty() {
		return historyDuty;
	}

	public float getAvailableFactor() {
		return availableFactor;
	}
	
	public static int getNumberOfZeroAvailableFactor() {
		return numberOfZeroAvailableFactor;
	}

	public static void setNumberOfZeroAvailableFactor(int numberOfZeroAvailableFactor) {
		ForDispatch.numberOfZeroAvailableFactor = numberOfZeroAvailableFactor;
	}

	public String toString()
	{
		return " : capactity is "+capactity+ ", isActive is "+isActive+", currentDuty is "+
				   currentDuty+", capacityUsageRate is "+capacityUsageRate + " historyDuty is "+historyDuty+" dispatchType is "+dispatchType+" weightFactor is " +weightFactor
				   +" availableFactor is "+availableFactor;
	}
	
}
