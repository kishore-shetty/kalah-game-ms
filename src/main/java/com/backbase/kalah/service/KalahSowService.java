/*
 *  /*
 *  * Copyright (c) 2021 /  Kishore B Shetty
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *          http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.backbase.kalah.service;

import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.model.PlayerEnum;
import com.backbase.kalah.repository.KalahGameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backbase.kalah.constants.KalahConstants.*;

@Service
@Slf4j
public class KalahSowService {

    private final KalahGameRepository kalahGameRepository;

    public KalahSowService(KalahGameRepository kalahGameRepository) {
        this.kalahGameRepository = kalahGameRepository;
    }

    /**
     * This service sow the seeds from given pitId to its right.
     * @param kalahGame - Object that is saved in mongo
     * @param pitId - pitId - Index of the pit
     * @return kalahGame - Object that is saved in mongo
     */
    public KalahGame sowSeeds(KalahGame kalahGame, Integer pitId) {

        if (kalahGame.getPlayer() == PlayerEnum.PLAYER_FIRST && pitId > PLAYER1_STORE ||
                kalahGame.getPlayer() == PlayerEnum.PLAYER_SECOND && pitId < PLAYER1_STORE) {
            log.error("Incorrect pitId selected to sow - {}", pitId);
            throw new KalahException(ErrorTypeEnum.KGE_02);
        }

        Pit pit = kalahGame.getPit(pitId);

        if (KalahConstants.EMPTY_SEEDS == pit.getSeeds()) {
            log.error("pitId selected to sow is empty - {}", pitId);
            throw new KalahException(ErrorTypeEnum.KGE_03);
        }

        int seeds = pit.getSeeds();
        pit.setSeeds(KalahConstants.EMPTY_SEEDS);
        kalahGame.setCurrentPitIndex(pitId);

        for (int i = 0; i < seeds - 1; i++) {
            startSowing(kalahGame, false);
        }

        startSowing(kalahGame, true);

        /*
         * When last seed ends up in current players store, then current player gets one more chance to sow.
         */
        if (kalahGame.getCurrentPitIndex() != PLAYER1_STORE && kalahGame.getCurrentPitIndex() != KalahConstants.PLAYER2_STORE){
            kalahGame.setPlayer(kalahGame.getPlayer() == PlayerEnum.PLAYER_FIRST ? PlayerEnum.PLAYER_SECOND : PlayerEnum.PLAYER_FIRST);
        }

        /*
         * Check the current status of game,
         * check if the game needs to be ended.
         */
        checkGamePosition(kalahGame);

        kalahGameRepository.save(kalahGame);
        return kalahGame;
    }


    private void startSowing(KalahGame kalahGame, boolean isLastSeed) {

        int pitIndex = kalahGame.getCurrentPitIndex() % KalahConstants.TOTAL_PITS + 1;

        if(kalahGame.getPlayer() == PlayerEnum.PLAYER_FIRST && pitIndex == KalahConstants.PLAYER2_STORE ||
                kalahGame.getPlayer() == PlayerEnum.PLAYER_SECOND && pitIndex == PLAYER1_STORE){
            pitIndex = pitIndex % KalahConstants.TOTAL_PITS + 1;
        }

        kalahGame.setCurrentPitIndex(pitIndex);

        Pit pit = kalahGame.getPit(pitIndex);

        if(!isLastSeed || PLAYER1_STORE == pitIndex || KalahConstants.PLAYER2_STORE == pitIndex) {
            pit.sow();
            return;
        }

        /*
         * If its last seed, We have to check the opponents pit & current players pit
         * i.e if: last seed && current players pit is empty & opponents pit that is opposite to current player has seed.
         * then: last seed & opponents seed will go to current players store
         */

        Pit opponentsPit = kalahGame.getPit(KalahConstants.TOTAL_PITS - pitIndex);

        if(pit.checkIfEmpty() && isPitOwner(kalahGame.getPlayer(), pitIndex) && !opponentsPit.checkIfEmpty()){
            log.info("Pit: {} with opponent pitIndex - {} & seeds - {}", pitIndex, opponentsPit.getIndex(), opponentsPit.getSeeds());
            int storeIndex = kalahGame.getPlayer() == PlayerEnum.PLAYER_FIRST ? PLAYER1_STORE : KalahConstants.PLAYER2_STORE;
            Pit pitStore = kalahGame.getPit(storeIndex);
            log.info("Pit Store had seeds:  {} ", pitStore.getSeeds());
            pitStore.addSeeds(opponentsPit.getSeeds() + 1);
            opponentsPit.setSeeds(0);
        }else{
            pit.sow();
        }

    }

    private boolean isPitOwner(PlayerEnum player, int pitIndex) {
        if(player == PlayerEnum.PLAYER_FIRST && pitIndex > 0 && pitIndex < PLAYER1_STORE
                || player == PlayerEnum.PLAYER_SECOND && pitIndex > PLAYER1_STORE && pitIndex < KalahConstants.PLAYER2_STORE){
            return true;
        }
        return false;
    }

    /**
     * When one player no longer has any seeds in any of their houses, the game ends.
     * The other player moves all remaining seeds to their store.
     * The player with the most seeds in their store wins.
     * @param kalahGame - Object that is saved in mongo
     */
    private void checkGamePosition(KalahGame kalahGame){

        int player1Count = 0;
        int player2Count = 0;

        for(Pit pit : kalahGame.getPits()){
            if(pit.getIndex() >= PLAYER1_FIRST_HOUSE && pit.getIndex() < PLAYER1_STORE){
                player1Count = player1Count + pit.getSeeds();
            }
            if(pit.getIndex() >= PLAYER2_FIRST_HOUSE && pit.getIndex() < PLAYER2_STORE){
                player2Count = player2Count + pit.getSeeds();
            }
        }

        if(player1Count == 0){
            kalahGame.getPit(PLAYER1_STORE).addSeeds(player2Count);
            kalahGame.setGameStatus(STATUS_END);
            kalahGame.clearAllPit();
            findAndUpdateWinner(kalahGame);
            return;
        }

        if(player2Count == 0){
            kalahGame.getPit(PLAYER2_STORE).addSeeds(player1Count);
            kalahGame.setGameStatus(STATUS_END);
            kalahGame.clearAllPit();
            findAndUpdateWinner(kalahGame);
        }

    }

    private void findAndUpdateWinner(KalahGame kalahGame){

        Pit player2Pit = kalahGame.getPit(PLAYER2_STORE);
        Pit player1Pit = kalahGame.getPit(PLAYER1_STORE);

        if(player2Pit.getSeeds() < player1Pit.getSeeds()){
            kalahGame.setGameWinner(PlayerEnum.PLAYER_FIRST.toString());
        }else if(player2Pit.getSeeds() > player1Pit.getSeeds()){
            kalahGame.setGameWinner(PlayerEnum.PLAYER_SECOND.toString());
        }else{
            kalahGame.setGameWinner(DRAW);
        }
    }

}
