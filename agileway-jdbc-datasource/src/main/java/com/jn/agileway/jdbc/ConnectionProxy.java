package com.jn.agileway.jdbc;

import com.jn.langx.Delegatable;

import java.sql.Connection;

public interface ConnectionProxy extends Connection, Delegatable<Connection> {

}
