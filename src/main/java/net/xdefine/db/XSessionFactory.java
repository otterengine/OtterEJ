package net.xdefine.db;

public interface XSessionFactory {

	XSession getCurrentSession();
	XSession getCurrentSession(String name);
	
	void setCurrentNullIfTarget(String name, XSession dbSessionImpl);
	
	
}
