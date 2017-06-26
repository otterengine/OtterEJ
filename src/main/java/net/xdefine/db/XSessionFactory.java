package net.xdefine.db;

public interface XSessionFactory {

	XSession openSession();
	XSession getCurrentSession(String name);
	
	void setCurrentNullIfTarget(String name, XSession dbSessionImpl);
	
	
}
