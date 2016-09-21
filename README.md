# restserver-with-netty
maven+netty+srping实现基于json的rest服务

设计思想

  1、采用约定大于配置的原则实现
  
    uri约束：/context/class/method,其中context预留做SBL
	
    请求数据约束：post+json，其中json可为空
	
  2、请求处理实现
  
    采用线程安全的单例模式处理请求
	
    根据uri里的class通过spring加载bean
	
    通过反射调用class对应的method
	