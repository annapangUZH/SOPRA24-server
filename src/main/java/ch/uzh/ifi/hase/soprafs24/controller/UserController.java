package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.exceptions.UserNotFoundException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserTokenDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    String token = userService.doLogin(user);
    UserTokenDTO tokenDTO = new UserTokenDTO();
    tokenDTO.setToken(token);
    // convert internal representation of user back to API
    return tokenDTO;
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logoutUser(@RequestBody UserTokenDTO userTokenDTO) {
    userService.doLogout(userTokenDTO.getToken());
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserById(@PathVariable Long userId) throws UserNotFoundException {
    Optional<User> user = userService.getUserById(userId);
    if (user.isEmpty()) {
      throw new UserNotFoundException(userId);
    }
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user.get());
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void editUser(@PathVariable Long userId, @RequestBody UserPutDTO userPutDTO) throws UserNotFoundException {
      Optional<User> user = userService.getUserById(userId);
      if (user.isEmpty()) {
          throw new UserNotFoundException(userId);
      }
      userService.validateUserToken(userPutDTO.getToken(), user.get().getToken());
      userService.updateUser(user.get(), userPutDTO.getBirthday(), userPutDTO.getUsername());
  }

}
