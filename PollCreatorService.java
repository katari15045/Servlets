package com.github.katari15045;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;


public class PollCreatorService 
{
	private Database database;
	private PreparedStatement preparedStatementForTable;
	private PreparedStatement preparedStatementForData;
	
	private int billID;
	private String groupName;
	private String memberName;
	private Date expiryDate;
	private boolean monthlyExpensePaymentStatus;
	private boolean loanRequestStatus;
	
	public boolean start(HttpServletRequest request) throws ClassNotFoundException, SQLException
	{
		getDataFromRequest(request);
		
		return storeData();
	}
	
	private void getDataFromRequest(HttpServletRequest request)
	{
		billID = Integer.parseInt( request.getParameter("billID") );
		groupName = request.getParameter("groupName");
		memberName = request.getParameter("memberName");
		expiryDate = Date.valueOf( request.getParameter("expiryDate") );
		
		if( request.getParameter("monthlyExpensePaymentStatus").equals("yes") )
		{
			monthlyExpensePaymentStatus = true;
		}
		
		else
		{
			monthlyExpensePaymentStatus = false;
		}
		
		if( request.getParameter("loanRequest").equals("yes") )
		{
			loanRequestStatus = true;
		}
		
		else
		{
			loanRequestStatus = false;
		}
	}
	
	private boolean storeData() throws ClassNotFoundException, SQLException
	{
		database = new Database();
		database.makeConnection();
		
		prepareCommands();
		database.executeUpdate(preparedStatementForTable);
		
		if( doesPollAlreadyExist() )
		{
			database.closeConnection();
			return false;
		}
		
		database.executeUpdate(preparedStatementForData);
		database.closeConnection();
		return true;
	}
	
	private void prepareCommands() throws SQLException 
	{
		prepareCommandForTableCreation();
		prepareCommandForInsertingData();
	}
	
	private void prepareCommandForTableCreation() throws SQLException
	{
		preparedStatementForTable = database.getConnection().prepareStatement("create table if not exists poll(Bill_id int primary key, Group_name varchar(20), Mem_name varchar(20), Expiry_date Date, Monthly_expense_payment_status bool, Loan_request_status bool );");
	}
	
	private void prepareCommandForInsertingData() throws SQLException
	{
		preparedStatementForData =  database.getConnection().prepareStatement("insert into poll values(?,?,?,?,?,?)");
		preparedStatementForData.setInt(1, billID);
		preparedStatementForData.setString(2, groupName);
		preparedStatementForData.setString(3, memberName);
		preparedStatementForData.setDate(4, expiryDate);
		preparedStatementForData.setBoolean(5, monthlyExpensePaymentStatus);
		preparedStatementForData.setBoolean(6, loanRequestStatus);		
	}
	
	private boolean doesPollAlreadyExist() throws SQLException
	{
		PreparedStatement preparedStatement = database.getConnection().prepareStatement("select count(*) from poll where Bill_id=" + billID + ";");
		ResultSet resultSet = database.executeQuery(preparedStatement);
		resultSet.next();
		
		if( resultSet.getInt(1) == 0 )
		{
			return false;
		}
		
		return true;
	}
}
