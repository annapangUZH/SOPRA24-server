package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public Optional<User> getUserById(long userId){
    return this.userRepository.findById(userId);
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDate.now());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }


  public String doLogin(User user){
    User databaseUser = userRepository.findByUsername(user.getUsername());
    if (databaseUser == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    if (!databaseUser.getPassword().equals(user.getPassword())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid username or password");
    }
    databaseUser.setStatus(UserStatus.ONLINE);
    userRepository.save(databaseUser);
    userRepository.flush();
    return databaseUser.getToken();
  }

    public void doLogout(String token){
        User databaseUser = userRepository.findByToken(token);
        if (databaseUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        databaseUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(databaseUser);
        userRepository.flush();
    }
  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }

    public void validateUserToken(String remoteToken, String localToken) {
      if (!remoteToken.equals(localToken)){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "usertoken does not match");
      }
    }

    public void updateUser(User user, String birthday, String username) {
      user.setBirthday(birthday);
      user.setUsername(username);
      userRepository.save(user);
      userRepository.flush();
    }
}
