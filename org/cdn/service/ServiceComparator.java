/**
 * 实现业务部件按照可用因子排序能力（按照可用率排序能够更均衡调度）
 */
package org.cdn.service;

import java.util.Comparator;

/**
 * @author XianCheng
 *
 */
public class ServiceComparator implements Comparator<Service>{
	
	@Override
	public int compare(Service o1, Service o2) {
		// TODO Auto-generated method stub
			return (int) (o2.getForDispatch().availableFactor*1000000-o1.getForDispatch().availableFactor*1000000);
    }
}
