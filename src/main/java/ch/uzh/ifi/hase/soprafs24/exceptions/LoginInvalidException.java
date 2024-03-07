package ch.uzh.ifi.hase.soprafs24.exceptions;

import java.io.IOException;

public class LoginInvalidException extends IOException {
  private final String username;
  public LoginInvalidException(String username){
    super("invalid username or password");
    this.username = username;
  }
  public String getUsername(){
    return username;
  }
}
