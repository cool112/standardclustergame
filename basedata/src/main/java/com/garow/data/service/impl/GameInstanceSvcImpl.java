package com.garow.data.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.garow.data.model.GameInstance;
import com.garow.data.repository.GameInstanceRepo;
import com.garow.data.service.GameInstanceSvc;

@Service
public class GameInstanceSvcImpl implements GameInstanceSvc {
	@Autowired
	private GameInstanceRepo gameInstanceRepo;

	@Override
	public String insert(GameInstance game) {
		GameInstance newGame = gameInstanceRepo.insert(game);
		return newGame.getId();
	}

	@Override
	public GameInstance findById(String id) {
		Optional<GameInstance> result = gameInstanceRepo.findById(id);
		if (result.isPresent())
			return result.get();
		else
			return null;
	}

	@Override
	public void updateStatus(GameInstance game) {
		gameInstanceRepo.save(game);
	}

}
