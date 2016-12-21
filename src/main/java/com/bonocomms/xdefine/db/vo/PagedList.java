package com.bonocomms.xdefine.db.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.OrderEntry;

@SuppressWarnings("serial")
public class PagedList<T> extends ArrayList<T> implements List<T> {

	private Integer page;
	private Integer limit;
	private Long max;
	private Long min;
	private Long lastpage;
	private long rowSize;

	public PagedList() {
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static PagedList newInstance(Criteria cr, int page, int limit) {
		PagedList list = new PagedList(cr.list());
		list.setPage(page, limit);
		list.setRowSize(cr);
		return list;
	}

	public PagedList(List<T> selectList) {
		this.addAll(selectList);
	}

	public Long getMax() {
		return max;
	}

	public Long getMin() {
		return min;
	}
	
	public Long getPrevpage() {
		return (long) ((page == 1) ? 1 : page - 1);
	}

	public Long getNextpage() {
		return (page + 1 >= lastpage) ? lastpage : page + 1;
	}

	public Long getLastpage() {
		return lastpage;
	}
	
	public void setList(List<T> list) {
		this.addAll(list);
	}
	
	public long getRowSize() {
		return rowSize;
	}

	public void setRowSize(Criteria cr) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<OrderEntry> orderIter = ((CriteriaImpl) cr).iterateOrderings();
			while (orderIter.hasNext()) {
			    orderIter.next();
			    orderIter.remove();
			}
		}
		catch(Exception ex) {
		}
		
		Criteria cr1 = cr.setFirstResult(0).setProjection(Projections.rowCount());
		Object res = cr1.uniqueResult();
		this.setRowSize(res == null ? 0 : Long.parseLong(res.toString()));
	}

	public void setRowSize(long rowSize) {
		this.rowSize = rowSize;
		if (this.rowSize >= 0 && this.page > 0 && this.limit > 0) {
			this.setPageRange(this.page, (long) Math.ceil((float) this.rowSize / this.limit));
		}
	}

	private void setPageRange(long page, long pageSize) {

		if (pageSize == 0) pageSize = 1;
		this.lastpage = pageSize;

		// page 표시
		long min;
		long max;
		if (pageSize < 6) {
			min = 1;
			max = pageSize;
		} else if (page - 2 > 0) {
			if (page + 2 < pageSize) {
				min = page - 2;
				max = page + 2;
			} else {
				min = pageSize - 4;
				max = pageSize;
			}
		} else {// 필요없을 것 같은데...
			long value = 3 - page;

			if (page + value > pageSize) {
				min = 1;
				max = pageSize;
			} else {
				min = 1;
				max = page + 2 + value;
			}
		}
		
		this.min = min;
		this.max = max;
				
	}

	public Integer getPage() {
		return page;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public long getPageGap() {
		return this.rowSize - ((this.page - 1) * this.limit);
	}

	public void setPage(int page, int limit) {
		this.page = page;
		this.limit = limit;
		if (this.rowSize >= 0 && this.page > 0 && this.limit > 0) {
			this.setPageRange(this.page, (long) Math.ceil((float) this.rowSize / this.limit));
		}
	}
	
	public long number(int n) {
		return this.rowSize - ((this.page - 1) * this.limit) - n;
	}

	public long getStart() {
		return 1 + ((page - 1) * limit);
	}
	
	public long getEnd() {
		long end = (page * limit);
		return (end > this.rowSize ? this.rowSize : end);
	}


}
