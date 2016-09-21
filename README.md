# restserver-with-netty
maven+netty+srping实现基于json的rest服务

开发背景

  1、项目运行不在依赖于web容器
  
  2、基于netty的nio模式，使用应用更加轻量，且并发支持更加优秀
  
  3、开发更加简单快捷

设计思想

  1、采用约定大于配置的原则实现
  
    uri约束：/context/class/method,其中context预留做SBL
	
    请求数据约束：post+json，其中json可为空
	
  2、请求处理实现
  
    采用线程安全的单例模式处理请求
	
    根据uri里的class通过spring加载bean
	
    通过反射调用class对应的method
	