package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class MyFilter3 implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		System.out.println("MyFilter3 doFilter");
		filterChain.doFilter(servletRequest, servletResponse);
		//만약 필터를 걸지 않고 여기서 프로그램을 실행하면 여기서 프로그램이 끝나버립니다.
		//PrintWriter out = servletResponse.getWriter();
		//out.print("필터1");
		//즉 프로세스를 끝내지 않고 계속 진행하려면 체인에 넘겨줘야만 합니다.
	}

}
