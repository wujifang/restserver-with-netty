package wjf.rest.service;

import org.springframework.stereotype.Service;

@Service
public class TestService extends BaseService {

	public Object hello(String json) {
		return "hello" + json;
	}

	public Object hello1() {
		return "hello world";
	}
}
