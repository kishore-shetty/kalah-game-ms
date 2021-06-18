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

package com.backbase.kalah.model;

import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

import static com.backbase.kalah.constants.KalahConstants.*;

@Document("KalahGame")
@Data
@Slf4j
public final class KalahGame {

    @Id
    private String id;

    private List<Pit> pits;

    private PlayerEnum player;

    @JsonIgnore
    private int currentPitIndex;

    private String gameStatus;

    private String gameWinner;

    public KalahGame() {
        this (INITIAL_HOUSE_SEEDS, PlayerEnum.PLAYER_FIRST);
    }

    public KalahGame(int pitSeeds, PlayerEnum playerEnum) {
        this.pits = Arrays.asList(
                new Pit(PLAYER1_FIRST_HOUSE, pitSeeds),
                new Pit(PLAYER1_SECOND_HOUSE, pitSeeds),
                new Pit(PLAYER1_THIRD_HOUSE, pitSeeds),
                new Pit(PLAYER1_FOURTH_HOUSE, pitSeeds),
                new Pit(PLAYER1_FIFTH_HOUSE, pitSeeds),
                new Pit(PLAYER1_SIXTH_HOUSE, pitSeeds),
                new Pit(PLAYER1_STORE,0),
                new Pit(PLAYER2_FIRST_HOUSE, pitSeeds),
                new Pit(PLAYER2_SECOND_HOUSE, pitSeeds),
                new Pit(PLAYER2_THIRD_HOUSE, pitSeeds),
                new Pit(PLAYER2_FOURTH_HOUSE, pitSeeds),
                new Pit(PLAYER2_FIFTH_HOUSE, pitSeeds),
                new Pit(PLAYER2_SIXTH_HOUSE, pitSeeds),
                new Pit(PLAYER2_STORE,0));
        this.player = playerEnum;
        this.gameStatus = STATUS_ON;
        this.gameWinner = AWAITED;
    }

    public KalahGame(PlayerEnum playerEnum) {
        this (INITIAL_HOUSE_SEEDS, playerEnum);
    }

    public Pit getPit(Integer pitIndex) throws KalahException {
        try {
            return this.pits.get(pitIndex-1);
        }catch (Exception e){
            log.error("Exception while selecting the pit to sow - {}", pitIndex);
            throw new KalahException(ErrorTypeEnum.KGE_02);
        }
    }

    public void clearAllPit() throws KalahException {
       for(Pit pit : this.pits){
           if(pit.getIndex() == PLAYER1_STORE || pit.getIndex() == PLAYER2_STORE) {
               continue;
           }
           pit.setSeeds(0);
       }
    }

}
