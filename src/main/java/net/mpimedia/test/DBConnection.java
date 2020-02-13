package net.mpimedia.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConnection {

	
	public static void main(String args[]){  
		try{  
		Class.forName("com.mysql.cj.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
		"jdbc:mysql://db4free.net:3306/mpimedianet","mpimedianet","dakwahmedia");  
		//here sonoo is database name, root is username and password  
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("select * from division");  
		while(rs.next())  
		System.out.println(rs.getInt(1)+"  "+rs.getString("name")+"  "+rs.getString(3));  
		con.close();  
		}catch(Exception e){ System.out.println(e);}  
		}  
}
