package com.garow.gameserver.ctrl;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.auth.utils.SessionUtils;
import com.garow.data.model.GameInstance;
import com.garow.data.model.Player;
import com.garow.data.service.GameInstanceSvc;
import com.garow.data.service.PlayerSvc;
import com.garow.data.service.RankSvc;

/**
 * 玩家接口控制器, turn to UserCtrl
 * 
 * @author seg
 *
 */
@Deprecated
@RestController
@RequestMapping("/player")
public class PlayerCtrl {
	@Autowired
	private PlayerSvc		playerSvc;
	@Autowired
	private GameInstanceSvc	gameInstanceSvc;
	@Autowired
	private RankSvc			rankSvc;

	@RequestMapping("/add")
	public String addNewPlayer(Player player) {
		playerSvc.newPlayer(player);
		return "success";
	}

	@RequestMapping("/getOne")
	public Player getOne(String deviceId, String app, @RequestBody Map<String, Object> params) {
		if (deviceId == null)
			deviceId = (String) params.get("deviceId");
		if (app == null)
			app = (String) params.get("app");
		return playerSvc.findPlayerByDevAndApp(deviceId, app);
	}

	/**
	 * 更新玩家分数,需要对应游戏流水号,防止重复提交和恶意提交
	 * 
	 * @param gameId
	 * @param score
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateScore")
	public String updateScore(String gameId, int score, HttpServletRequest request) {
		Player player = SessionUtils.getPlayer(request.getSession());
		if (player == null)
			return "null";
		GameInstance game = gameInstanceSvc.findById(gameId);
		if (game == null || game.getStatus() >= GameInstanceSvc.STATUS_END)
			return "end";
		int oldScore = player.getScore();
		player.setScore(score);
		if (playerSvc.updateScore(player)) {
			rankSvc.updateScore(player.getApp(), oldScore, score);
			game.setStatus(GameInstanceSvc.STATUS_END);
			gameInstanceSvc.updateStatus(game);
		}
		return "success";
	}

	/**
	 * 做md5校验
	 * 
	 * @param md5
	 * @param archive
	 * @param request
	 * @return
	 */
	@RequestMapping("/save")
	public String archive(String md5, String archive, HttpServletRequest request) {
		Player player = SessionUtils.getPlayer(request.getSession());
		if (player == null)
			return "null";
		player.setArchive(archive);
		player.setUpdateTime(new Date());
		playerSvc.update(player);
		return "success";
	}
}
