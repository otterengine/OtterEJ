package net.xdefine.db.interceptors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.xdefine.db.XSession;
import net.xdefine.db.XSessionFactory;

@Aspect
@Component
public class ServiceInterceptor implements InitializingBean {
	
	@Autowired private XSessionFactory sessionFactory;
	
    @Override
	public void afterPropertiesSet() throws Exception {
	}

	@Around(value = "@annotation(transactional)", argNames = "transactional")
    public Object proceed(ProceedingJoinPoint pjp, Transactional transactional) throws Throwable {
		
		XSession session = sessionFactory.getCurrentSession();
		session.setReadOnly(transactional.readOnly());
		
		try {
	    	return pjp.proceed();
		}
		catch(Exception ex) {
			session.close();
			session = null;
			throw ex;
		}
		finally {
			if (session != null) {
				session.close();
				session = null;
			}
		}
    }
}
