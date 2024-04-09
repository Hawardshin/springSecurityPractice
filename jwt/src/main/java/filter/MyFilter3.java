package filter;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter {
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		// System.out.println("MyFilter3 doFilter");
		// filterChain.doFilter(servletRequest, servletResponse);
		//만약 필터를 걸지 않고 여기서 프로그램을 실행하면 여기서 프로그램이 끝나버립니다.
		//PrintWriter out = servletResponse.getWriter();
		//out.print("필터1");
		//즉 프로세스를 끝내지 않고 계속 진행하려면 체인에 넘겨줘야만 합니다.

		System.out.println("필터3");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		req.setCharacterEncoding("utf-8");
		HttpServletResponse res = (HttpServletResponse)servletResponse;
		if (req.getMethod().equals("POST")) {
			String HeaderAuth = req.getHeader("Authorization");
			System.out.println("HeaderAuth : " + HeaderAuth);
			if (HeaderAuth.equals("cos")) {
				System.out.println("cos");
				filterChain.doFilter(req, res);
			}
			else {
				PrintWriter out = res.getWriter();
				out.print("인증안됨");
			}
		}
	}

}
