package wjf.restserver;

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import wjf.rest.service.BaseService;

/**
 * @author wujf 线程安全的单例模式处理请求
 */
public class ProcessReq {
	private static ProcessReq instance = null;
	ApplicationContext cx;
	private static final Logger log = LoggerFactory.getLogger(ProcessReq.class);

	private ProcessReq() {
		log.info("开始初始化spring容器");
		// 加载spring
		cx = new ClassPathXmlApplicationContext("spring.xml");
		log.info("开始初始化spring容器结束");
	}

	public static ProcessReq getInstance() {
		if (instance == null) {
			synchronized (ProcessReq.class) {
				ProcessReq temp = instance;
				if (temp == null) {
					temp = new ProcessReq();
					instance = temp;
				}
			}
		}
		return instance;
	}

	public Object process(String uri, String json) {
		// TODO uri约束：/context/class/method,其中context预留做SBL
		String uuid = UUID.randomUUID().toString();
		Object retmsg;
		try {
			log.info("uuid:<{}>,收到请求url:{},请求数据：{}", uuid, uri, json);
			String[] arry = uri.split("/");
			if (arry.length != 4)
				throw new Exception("uri处理异常，预期格式为/context/class/method");
			if (!cx.containsBean(arry[2]))
				throw new Exception("找不到指定的class，请检查请求的uri");
			BaseService base = (BaseService) cx.getBean(arry[2]);
			Class<? extends BaseService> cls = base.getClass();
			Method method;
			// 如果没有请求参数
			if (StringUtils.isEmpty(json)) {
				method = cls.getDeclaredMethod(arry[3]);
				if (method == null)
					throw new Exception("找不到指定的method，请检查请求的uri");
				retmsg = method.invoke(base);
			} else {
				method = cls.getDeclaredMethod(arry[3], String.class);
				if (method == null)
					throw new Exception("找不到指定的method，请检查请求的uri");
				retmsg = method.invoke(base, json);
			}
		} catch (Exception ex) {
			retmsg = "请求处理异常，异常信息：" + ex.getMessage();
		}
		log.info("uuid:<{}>,返回数据:{}", uuid, retmsg);
		return retmsg;
	}
}
