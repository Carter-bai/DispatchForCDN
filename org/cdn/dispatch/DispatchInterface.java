/**
 * 定义调度部件能力接口
 */
package org.cdn.dispatch;

import java.util.List;

import org.cdn.user.User;

/**
 * @author XianCheng
 *
 */
public interface DispatchInterface {

public int dispatch(User user);//把待调度的列表内容进行调度

}
