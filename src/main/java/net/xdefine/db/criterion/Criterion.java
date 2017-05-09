package net.xdefine.db.criterion;

import java.io.Serializable;

import net.xdefine.db.vo.QueryObject;

public interface Criterion extends Serializable {

	QueryObject toSQLString();

}
