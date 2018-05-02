package org.binas.station.ws.it;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;

import org.binas.station.ws.AccountView;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidCredit_Exception;
import org.binas.station.ws.InvalidFormatEmail_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.UserDoesNotExists_Exception;
import org.junit.After;
import org.junit.Test;

public class GetBalanceIT extends BaseIT {
	@Test
	public void sucess() throws NoBinaAvail_Exception, BadInit_Exception, InvalidCredit_Exception, InvalidFormatEmail_Exception, InterruptedException, ExecutionException{
		client.testInit(3, 3, 30, 0);
		client.setBalance("mariana.mendes@gmail.com", 10, 0, 0);
		
		Response<GetBalanceResponse> response = client.getBalanceAsync("mariana.mendes@gmail.com");
		
		AccountView accountView = null;
		while(true){
			if(response.isDone()){
				accountView = response.get().getAccountInfo();
				break;
			}
		}
		assertNotNull(accountView);
		assertEquals(10, accountView.getCredit());
		assertEquals(0, accountView.getTag());
		assertEquals(0, accountView.getClientID());
	}
	
	@Test
	public void nullEmail(){
		Response<GetBalanceResponse> response = client.getBalanceAsync(null);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().getAccountInfo();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidFormatEmail_Exception){
						break;
					}
					else{
						fail();
					}
				}
			}
		}
	}
		
	@Test
	public void emptyEmail(){
		Response<GetBalanceResponse> response = client.getBalanceAsync("");
		
		while(true){
			if(response.isDone()){
				try {
					response.get().getAccountInfo();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidFormatEmail_Exception){
						break;
					}
					else{
						fail();
					}
				}
			}
		}
	}
	
	@Test
	public void blankEmail(){
		Response<GetBalanceResponse> response = client.getBalanceAsync("    ");
		
		while(true){
			if(response.isDone()){
				try {
					response.get().getAccountInfo();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidFormatEmail_Exception){
						break;
					}
					else{
						fail();
					}
				}
			}
		}
	}
	
	@Test
	public void wrongEmail(){
		Response<GetBalanceResponse> response = client.getBalanceAsync("wrongEmail");
		
		while(true){
			if(response.isDone()){
				try {
					response.get().getAccountInfo();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidFormatEmail_Exception){
						break;
					}
					else{
						fail();
					}
				}
			}
		}
	}
	
	@Test
	public void nonExistingEmail(){
		Response<GetBalanceResponse> response = client.getBalanceAsync("mariana.mendes@gmail.com");
		
		while(true){
			if(response.isDone()){
				try {
					response.get().getAccountInfo();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof UserDoesNotExists_Exception){
						break;
					}
					else{
						fail();
					}
				}
			}
		}
	}
	
	@After
	public void tearDown(){
		client.testClear();
	}
	
}
