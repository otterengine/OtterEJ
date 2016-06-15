package net.otterbase.oframework.auth.service;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.otterbase.oframework.OFContext;
import net.otterbase.oframework.annotation.LoginObject;
import net.otterbase.oframework.auth.vo.SignOnInterface;

public class MemberDetailsServiceImpl implements UserDetailsService {

	@Autowired private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().begin();
		
		Class<?> accountClass = OFContext.getSignOnClass();
		SignOnInterface account = null;
		
		try {
			
			String nameField = accountClass.getAnnotation(LoginObject.class).nameField();
			String[] trueFields = accountClass.getAnnotation(LoginObject.class).trueFields();
			
			Criteria cr = session.createCriteria(accountClass)
							.add(Restrictions.like(nameField, userId.toLowerCase()))
							.setMaxResults(1);
			
			for (String trueField : trueFields) {
				cr = cr.add(Restrictions.eq(trueField, true));
			}
			
			account = (SignOnInterface) cr.uniqueResult();
			session.getTransaction().commit();
		}
		catch(HibernateException ex) {
			session.getTransaction().rollback();
			ex.printStackTrace();
		}

		if (account == null) throw new UsernameNotFoundException("user not found");	
		return account.toSignedDetails();
		
	}

}
