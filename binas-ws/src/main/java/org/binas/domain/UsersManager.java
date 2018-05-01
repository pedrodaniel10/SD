package org.binas.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.binas.ws.UserView;

import exceptions.BadInitException;
import exceptions.EmailExistsException;
import exceptions.InvalidEmailException;
import exceptions.UserNotExistsException;

public class UsersManager {
	// Singleton -------------------------------------------------------------

	private UsersManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		private static final UsersManager INSTANCE = new UsersManager();
	}

	public static synchronized UsersManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	// ------------------------------------------------------------------------
	
	private static int DEFAULT_INITIAL_BALANCE = 10;
	private AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);

	/**HashMap to save the users*/ 
	private static ConcurrentHashMap<String,User> users = new ConcurrentHashMap<>();
	
	/**Adds a new user to the map
	 * @param email
	 * @return user view
	 * @throws InvalidEmailException
	 * @throws EmailExistsException
	 */
	public synchronized UserView addUser(String email) throws InvalidEmailException, EmailExistsException{
		if(stringNullOrEmpty(email)){
			throw new InvalidEmailException("The email can not be null or empty.");
		}
		if(users.get(email) == null){
			User user = new User(email, initialBalance.get());
			users.put(email, user);
			return user.getUserView();
		}
		else{
			throw new EmailExistsException("The email " + email + " is already taken.");
		}
	}
	
	/**
	 * @param email
	 * @return user
	 * @throws UserNotExistsException
	 * @throws  
	 */
	public User getUser(String email) throws UserNotExistsException {
		if(stringNullOrEmpty(email)){
			throw new UserNotExistsException("The email can not be null or empty.");
		}
		User user = users.get(email);
		if(user == null){
			throw new UserNotExistsException("The user with email " + email + " doesn't exists.");
		}
		return user;
	}
		
	public synchronized void init(int newCreditBegin) throws BadInitException{
		if(newCreditBegin < 0){
			throw new BadInitException("Initial points can not be negative.");
		}
		initialBalance.set(newCreditBegin);
	}
	
	public synchronized void reset(){
		users.clear();
		initialBalance.set(DEFAULT_INITIAL_BALANCE);
	}
	
	// Auxiliary Functions
	private boolean stringNullOrEmpty(String string){
		if(string == null || string.trim().equals("")){
			return true;
		}
		return false;
	}
}
