package org.springframework.samples.parchisoca.player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    private final String PLAYERS_LISTING_VIEW= "players/playersListing";
    private final String CREATE_PLAYERS = "players/createPlayerForm";
    private final String EDIT_PLAYER = "players/editPlayer";
    private final String LOGGED_USER_VIEW = "players/myProfile";
    private final String PLAYER_PROFILE = "players/playerProfile";
    private final String FRIEND_PROFILE = "players/friendProfile";
    private final String FIND_PLAYER_VIEW = "players/findPlayer";
    private final String PLAYER_FRIENDS = "players/myFriends";
    private final String MESSAGE = "message";
    private final String PLAYER_NOT_FOUND = "Player not found";


    @Autowired
    public PlayerController(PlayerService playerService){
        this.playerService = playerService;
    }

    @GetMapping(value = "/players/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("player", new Player());
		return FIND_PLAYER_VIEW;
	}

    @GetMapping("/players/find/{username}")
    public ModelAndView findPlayer(@PathVariable("username") String username) {
        ModelAndView mav = new ModelAndView();
        Player player = playerService.findPlayersByUsername(username);
        String direction;
        if (player==null) {
            direction = "redirect:/error";
        } else {
            direction = PLAYER_PROFILE;
        }
        mav = new ModelAndView(direction);
        mav.addObject("player", player);
        return mav;
    }

    @GetMapping("/players/list")
    public ModelAndView showPlayers(){
        ModelAndView result = new ModelAndView(PLAYERS_LISTING_VIEW);
        List<Player> players = playerService.getPlayers();
        result.addObject("players", players);
        return result;
    }

    @GetMapping("/players/create")
    public ModelAndView createPlayer(){
        ModelAndView result = new ModelAndView(CREATE_PLAYERS);
        result.addObject("player", new Player());
        return result;
    }

    @PostMapping(value = "/players/create")
	public String processCreationForm(@Valid Player player, BindingResult result) {
		if (result.hasErrors()) {
			return CREATE_PLAYERS;
		}
		else {
			this.playerService.savePlayer(player);
			return "redirect:/welcome";
		}
	}

    @GetMapping("/players/{playerId}")
        public ModelAndView showPlayer(@PathVariable("playerId") int playerId) {
        ModelAndView mav = new ModelAndView(PLAYER_PROFILE);
        Optional<Player> player = this.playerService.findPlayerById(playerId);
        if (player.isPresent()) {
            mav.addObject(player.get());
        } else {
            mav.addObject(MESSAGE, PLAYER_NOT_FOUND);
        }
        return mav;
    }

    @GetMapping("/players/{playerId}/edit")
    public ModelAndView editLoggedPlayer(@PathVariable("playerId") int playerId) {
        Player player = playerService.getById(playerId);
        ModelAndView result = new ModelAndView(EDIT_PLAYER);
        result.addObject("player", player);
        return result;
    }

    @PostMapping("/players/{playerId}/edit")
    public String saveLoggedPlayer(@PathVariable("playerId") int playerId, Player player){
        Player playerToBeUpdated = playerService.getById(playerId);
        BeanUtils.copyProperties(player,playerToBeUpdated,"id","achievements", "user");
        playerService.savePlayer(playerToBeUpdated);
        return "redirect:/players/myProfile";

    }

    @GetMapping("/admin/{playerId}/edit")
    public ModelAndView editPlayer(@PathVariable("playerId") int playerId){
        Player player = playerService.getById(playerId);
        ModelAndView result=new ModelAndView(EDIT_PLAYER);
        result.addObject("player", player);
        return result;
    }

    @PostMapping("/admin/{playerId}/edit")
    public String savePlayer(@PathVariable("playerId") int playerId, Player player){
        Player playerToBeUpdated = playerService.getById(playerId);
        BeanUtils.copyProperties(player,playerToBeUpdated,"id","achievements","user");
        playerService.savePlayer(playerToBeUpdated);
        return "redirect:/players/{playerId}";
    }

    @GetMapping("/players/{playerId}/delete")
    public String deletePlayer(@PathVariable("playerId") int playerId) {
        playerService.deletePlayerById(playerId);
        return "redirect:/list";
    }

    @GetMapping("/players/myProfile")
    public ModelAndView showLoggedUser() {
        ModelAndView mav = new ModelAndView(LOGGED_USER_VIEW);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer id = this.playerService.getUserIdByName(username);
        Optional<Player> player = this.playerService.findPlayerById(id);
        if (player.isPresent()) {
            mav.addObject(player.get());
        } else {
            mav.addObject(MESSAGE, PLAYER_NOT_FOUND);
        }
        return mav;
    }

    @GetMapping("/players/myFriends")
    public ModelAndView showPlayersFriends() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer playerId = this.playerService.getUserIdByName(username);
        Player currentPlayer = playerService.getById(playerId);

        List<Player> friends = currentPlayer.getFriends();
        ModelAndView mav = new ModelAndView(PLAYER_FRIENDS);
        mav.addObject("friends", friends);
        return mav;
    }

    @GetMapping("players/{playerId}/viewFriend")
    public ModelAndView viewPlayerProfile(@PathVariable("playerId") Integer playerId) {
        Player player = playerService.getById(playerId);
        ModelAndView mav = new ModelAndView(FRIEND_PROFILE);
        mav.addObject("player", player);
        return mav;

    }

    @GetMapping("/players/{playerId}/add")
    public String addFriend(@PathVariable("playerId") Integer playerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer currentPlayerId = this.playerService.getUserIdByName(username);
        Player currentPlayer = playerService.getById(currentPlayerId);

        Player playerToAdd = playerService.getById(playerId);
        if (!currentPlayer.getFriends().contains(playerToAdd)) {
            currentPlayer.getFriends().add(playerToAdd);
            playerService.savePlayer(currentPlayer);
        }
        return "redirect:/players/myFriends";
    }

    @GetMapping("/players/friends/{playerId}/delete")
    public String deleteFriend(@PathVariable("playerId") Integer playerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer currentPlayerId = this.playerService.getUserIdByName(username);
        Player currentPlayer = playerService.getById(currentPlayerId);

        Player playerToDelete = playerService.getById(playerId);
        if (currentPlayer.getFriends().contains(playerToDelete)) {
            currentPlayer.getFriends().remove(playerToDelete);
            playerService.savePlayer(currentPlayer);
        }
        return "redirect:/players/myFriends";
    }


}
