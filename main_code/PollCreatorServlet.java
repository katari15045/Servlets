/*
 * Assumptions
 * 1. A HTTPSession is created immediately after a login
 * 2. (to be updated)A textBox is present for billID in "create poll for a bill" screen
 * 3. (to be updated)Poll table exists in MySQL Database with the schema poll(Bill_id int primary key, Group_name varchar(20), Mem_name varchar(20), Expiry_date Date, Monthly_expense_payment_status bool, Loan_request_status bool );
 * 4. (to be updated)No need of "From date" in  'Create poll' screen as poll starts instantly
 * 5. (to be updated)Need to create a separate  screen(& a servlet) for - viewing poll status and upvoting for a poll. 
 * 6. (to be updated)In 'creating poll screen', 'loan approval' radio button should be replaced with 'loan request' radio button
 */

package com.github.katari15045;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PollCreatorServlet")
public class PollCreatorServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private PollCreatorService pollCreatorService;
	private RequestDispatcher requestDispatcher;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if( isUserLoggedIn(request) )
		{
			pollCreatorService = new PollCreatorService();
			
			try 
			{
				if( !pollCreatorService.start(request) )
				{
					requestDispatcher = request.getRequestDispatcher("pollCreator.jsp");
					request.setAttribute("pollCreationStatus", "Poll already exists for this billID!!!");
					requestDispatcher.forward(request, response);
				}
				
				else
				{
					if( request.getParameter("loanRequest").equals("yes") )
					{
						requestDispatcher = request.getRequestDispatcher("loanRequest.jsp");
						requestDispatcher.forward(request, response);
					}
					
					else
					{
						requestDispatcher = request.getRequestDispatcher("poll.jsp");
						request.setAttribute("pollCreationStatus", "Poll created!!!");
						requestDispatcher.forward(request, response);	
					}
				}
			}
			
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
			
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		else
		{
			response.sendRedirect("login.jsp");
		}
	}

	private boolean isUserLoggedIn(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		
		if( session == null )
		{
			return false;
		}
		
		return true;
	}
}
