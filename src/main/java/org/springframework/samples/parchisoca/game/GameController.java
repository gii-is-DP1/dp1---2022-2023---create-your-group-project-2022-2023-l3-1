package org.springframework.samples.parchisoca.game;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisoca.board.OcaBoard;
import org.springframework.samples.parchisoca.board.OcaBoardController;
import org.springframework.samples.parchisoca.board.OcaBoardService;
import org.springframework.samples.parchisoca.board.ParchisBoardService;
import org.springframework.samples.parchisoca.dice.OcaDice;
import org.springframework.samples.parchisoca.piece.OcaPieceService;
import org.springframework.samples.parchisoca.player.Player;
import org.springframework.samples.parchisoca.player.PlayerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/games")
public class GameController {

    private final String GAMES_LISTING_VIEW = "/games/GamesListing";
    private final String GAME_WAIT_ROOM = "lobbys/waitRoom";
    private final String PARCHIS_INSTRUCTIONS_VIEW = "games/ParchisInstructions";
    private final String OCA_INSTRUCTIONS_VIEW = "games/OcaInstructions";
    private final String LOBBY = "/lobbys/createLobby";
    private final String PUBLIC_GAMES = "games/GamePublic";
    private final String GAMES_PLAYED = "games/GamesPlayed";
    private final String GAMES_IN_PROGRESS = "games/GamesInProgress";

    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private OcaBoardService ocaBoardService;
    @Autowired
    private OcaBoardController ocaBoardController;

    @GetMapping("/list")
    public ModelAndView showGames(){
        ModelAndView result = new ModelAndView(GAMES_LISTING_VIEW);
        result.addObject("games", gameService.getGames());
        //result.addObject("jugadores", service.getJugadores());
        return result;
    }

    @GetMapping("/create")
    public ModelAndView createProduct(){
        ModelAndView result = new ModelAndView(LOBBY);
        result.addObject("game", new Game());
        result.addObject("gameTypes", gameService.findAllGameTypes());
        return result;
    }
    
    @PostMapping("/create")
    public String saveGame(@Valid Game game, BindingResult result, ModelMap modelMap) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer id = playerService.getUserIdByName(username);
        Player currentPlayer = playerService.getById(id);

        if (result.hasErrors()) {
            modelMap.addAttribute("game", game);
            return LOBBY;
        }else{
            game.addPlayer(currentPlayer);
            game.setCreator(currentPlayer);
            game.setInProgress(true);
            this.gameService.save(game);
        }
        return "redirect:/games/lobby/"+game.getCode()+"/waitRoom";
    }

    @GetMapping("/lobbys")
    public ModelAndView publicGames(){
        ModelAndView result = new ModelAndView(PUBLIC_GAMES);
        result.addObject("games", gameService.findPublicGamesNotFinished());
        return result;
    }

    @GetMapping("/admin/lobbys/played")
    public ModelAndView gamesPlayed(){
        ModelAndView result = new ModelAndView(GAMES_PLAYED);
        result.addObject("games", gameService.findGamesFinished());
        return result;
    }

    @GetMapping("/admin/lobbys/inProgress")
    public ModelAndView gamesInProgress(){
        ModelAndView result = new ModelAndView(GAMES_IN_PROGRESS);
        result.addObject("games", gameService.findGamesInProgress());
        return result;
    }

    @GetMapping("/lobby/{code}")
    public String lobby(@PathVariable("code") String code, ModelMap model, HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer id = playerService.getUserIdByName(username);
        Player currentPlayer = playerService.getById(id);
        Game currentGame = gameService.findGameByCode(code);
        List<Player> ls = currentGame.getPlayers();
        System.out.println("N"+currentGame.getNumberOfPlayers());


        if (!currentGame.getPlayers().contains(currentPlayer)) {
            ls.add(currentPlayer);
            currentGame.setPlayers(ls);
            System.out.println("N"+currentGame.getNumberOfPlayers());
            gameService.save(currentGame);
            
            return "redirect:{code}/waitRoom";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/lobby/{code}/waitRoom")
    public ModelAndView waitRoom(@PathVariable("code") String code){
        Game currentGame = gameService.findGameByCode(code);
        int currentGameCreatorId = currentGame.getCreator().getId();
        Player currentCreator = gameService.findPlayerById(currentGameCreatorId);
        ModelAndView result = new ModelAndView(GAME_WAIT_ROOM);
        result.addObject("games", currentGame);
        result.addObject("creator", currentCreator);
        return result;
    }

    @GetMapping("/lobby/{code}/board")
    public String gameRoom(@PathVariable("code") String code, Map<String, Object> model){
        Game currentGame = gameService.findGameByCode(code);
        GameType currentGameType = currentGame.getGameType();

        if (currentGameType.getName().equals("PARCHIS")) {
            return "redirect:/boards/parchisBoard/{code}";
        } else {
            OcaBoard newOcaBoard = ocaBoardController.initBoard();
            currentGame.setOcaBoard(newOcaBoard);
            newOcaBoard.setGame(currentGame);
            ocaBoardService.save(newOcaBoard);
            gameService.save(currentGame);
            int ocaBoardId = newOcaBoard.getId();
            return "redirect:/boards/ocaBoard/"+ocaBoardId;
        }
    }

    @GetMapping("/instructions/parchisInstructions")
    public ModelAndView instructions(){
        ModelAndView result = new ModelAndView(PARCHIS_INSTRUCTIONS_VIEW);
        return result;
    }

    @GetMapping("/instructions/ocaInstructions")
    public ModelAndView instructionsOca(){
        ModelAndView result = new ModelAndView(OCA_INSTRUCTIONS_VIEW);
        return result;
    }


    

}
