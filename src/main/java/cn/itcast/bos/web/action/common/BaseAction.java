package cn.itcast.bos.web.action.common;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


public class BaseAction<T> extends ActionSupport implements ModelDriven<T>{
	
	protected T model;
	@Override
	public T getModel() {
		return model;
	}
	public BaseAction() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		Class<T> clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		try {
			model = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	// 属性驱动，获取当前页和当前页最多显示的记录数
		protected int page;
		protected int rows;

		public void setPage(int page) {
			this.page = page;
		}

		public void setRows(int rows) {
			this.rows = rows;
		}
	
		protected void pushPageDataToValueStack(Page<T> pageData){
			//返回客户端数据，需要total和rows
			Map<String, Object> result = new HashMap<>();
			result.put("total", pageData.getTotalElements());
			result.put("rows", pageData.getContent());
			//将map转换为json数据返回
			ActionContext.getContext().getValueStack().push(result);
		}
		
		protected void pushToValueStack(Object object){
			ActionContext.getContext().getValueStack().push(object);
		}
}
