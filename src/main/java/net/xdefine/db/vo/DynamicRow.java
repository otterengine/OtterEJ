package net.xdefine.db.vo;

import java.sql.ResultSet;
import java.util.HashMap;

@SuppressWarnings("serial")
public class DynamicRow extends HashMap<String, Object> {
	
	public DynamicRow() {
		
	}
	
	public DynamicRow(ResultSet rs) {
		try {
			
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				String label = rs.getMetaData().getColumnLabel(i);
				this.put(label, rs.getString(i));
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
	}

}
