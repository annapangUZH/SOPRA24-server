package ch.uzh.ifi.hase.soprafs24.exceptions;

import java.io.IOException;

public class UserNotFoundException extends IOException {
    private final long userId;
    public UserNotFoundException(long userId){
        super("user not found");
        this.userId = userId;
    }
    public long getUserId(){
        return userId;
    }
}
