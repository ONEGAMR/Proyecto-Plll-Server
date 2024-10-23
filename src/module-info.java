module Proyecto1 {
	requires javafx.controls;
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.fxml;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires javafx.base;
	requires java.sql;
    requires mysql.connector.java;

    exports domain;
	
	opens business to javafx.graphics, javafx.fxml;
}
