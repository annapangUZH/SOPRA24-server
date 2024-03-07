package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserPutDTO {
  private String username;
  private String birthday;

  private String token;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }


  public void setUsername(String username) {
        this.username = username;
    }
}
