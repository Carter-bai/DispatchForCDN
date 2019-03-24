/**
 * 定义调度辅助接口
 */
package org.cdn.dispatch;

import java.util.List;

/**
 * @author think
 *
 */
public interface DispathcAssistInterface<E> {
public List<E> doDispatchAssistent();//辅助调度,返回可用调度列表（已按可调度因子排序）
}
