package org.binas.station.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;

import org.binas.station.ws.AccountView;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.InvalidCredit_Exception;
import org.binas.station.ws.InvalidFormatEmail_Exception;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.UserDoesNotExists_Exception;
import org.junit.After;
import org.junit.Test;

public class SetBalanceIT extends BaseIT {
	
	@Test
	public void sucess() throws InterruptedException, ExecutionException, InvalidFormatEmail_Exception, UserDoesNotExists_Exception{
		Response<SetBalanceResponse> response = client.setBalanceAsync("mariana.mendes@gmail.com", 10, 0, 0);
		
		while(true){
			if(response.get().isBalanceBool()){
				AccountView accountView = client.getBalance("mariana.mendes@gmail.com");
				
				assertNotNull(accountView);
				assertEquals(10, accountView.getCredit());
				assertEquals(0, accountView.getTag());
				assertEquals(0, accountView.getClientID());
				break;
			}
		}
	}
	
	@Test
	public void olderTag() throws InterruptedException, ExecutionException, InvalidFormatEmail_Exception, UserDoesNotExists_Exception{
		Response<SetBalanceResponse> response = client.setBalanceAsync("mariana.mendes@gmail.com", 10, 1, 0);
		Response<SetBalanceResponse> response1 = client.setBalanceAsync("mariana.mendes@gmail.com", 20, 0, 0);
		
		while(true){
			if(response.get().isBalanceBool() && response1.get().isBalanceBool()){
				AccountView accountView = client.getBalance("mariana.mendes@gmail.com");
				
				assertNotNull(accountView);
				assertEquals(10, accountView.getCredit());
				assertEquals(1, accountView.getTag());
				assertEquals(0, accountView.getClientID());
				break;
			}
		}
	}
	
	@Test
	public void nullEmail(){
		Response<SetBalanceResponse> response = client.setBalanceAsync(null, 10, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
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
		Response<SetBalanceResponse> response = client.setBalanceAsync("", 10, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
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
		Response<SetBalanceResponse> response = client.setBalanceAsync("  ", 10, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
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
		Response<SetBalanceResponse> response = client.setBalanceAsync("wrongEmail", 10, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
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
	public void minusOneCredit(){
		Response<SetBalanceResponse> response = client.setBalanceAsync("mariana.mendes@gmail.com", -1, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidCredit_Exception){
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
	public void negativeCredit(){
		Response<SetBalanceResponse> response = client.setBalanceAsync("mariana.mendes@gmail.com", -20, 1, 0);
		
		while(true){
			if(response.isDone()){
				try {
					response.get().isBalanceBool();
					fail();
				} catch (InterruptedException e) {
					fail();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof InvalidCredit_Exception){
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
