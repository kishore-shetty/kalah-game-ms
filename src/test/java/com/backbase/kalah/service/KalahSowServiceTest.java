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

import com.backbase.kalah.exception.ErrorTypeEnum;
import com.backbase.kalah.exception.KalahException;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.model.PlayerEnum;
import com.backbase.kalah.repository.KalahGameRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.backbase.kalah.constants.KalahConstants.*;

@ExtendWith(MockitoExtension.class)
class KalahSowServiceTest {

    private KalahSowService subjectUnderTest;

    @Mock
    private KalahGameRepository kalahGameRepository;

    KalahGame kalahGame;

    @BeforeEach
    void setUp() {
        subjectUnderTest = new KalahSowService(kalahGameRepository);
        kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
    }

    @Test
    void sowSeeds_Exception() {
        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds(kalahGame,9));
        Assertions.assertEquals(ErrorTypeEnum.KGE_02.getErrorCode(), kalahException.getErrorCode());

        kalahGame = new KalahGame(PlayerEnum.PLAYER_SECOND);
        kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds(kalahGame,3));
        Assertions.assertEquals(ErrorTypeEnum.KGE_02.getErrorCode(), kalahException.getErrorCode());
    }

    @Test
    void sowSeeds() {
        Mockito.when(kalahGameRepository.save(Mockito.any())).thenReturn(kalahGame);

        kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        KalahGame response = subjectUnderTest.sowSeeds(kalahGame,1);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST, response.getPlayer());
        Assertions.assertEquals(STATUS_ON, response.getGameStatus());
        Assertions.assertEquals(0, response.getPit(PLAYER1_FIRST_HOUSE).getSeeds());
        Assertions.assertEquals(1, response.getPit(PLAYER1_STORE).getSeeds());
        Assertions.assertEquals(7, response.getPit(PLAYER1_SECOND_HOUSE).getSeeds());

        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.sowSeeds(kalahGame,1));
        Assertions.assertEquals(ErrorTypeEnum.KGE_03.getErrorCode(), kalahException.getErrorCode());

        kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        kalahGame.getPit(1).setSeeds(0);
        kalahGame.getPit(3).setSeeds(0);
        kalahGame.getPit(6).setSeeds(10);
        kalahGame.getPit(7).setSeeds(5);
        kalahGame.getPit(8).setSeeds(0);
        kalahGame.getPit(11).setSeeds(5);
        kalahGame.getPit(14).setSeeds(10);

        response = subjectUnderTest.sowSeeds(kalahGame,6);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_SECOND, response.getPlayer());
        Assertions.assertEquals(STATUS_ON, response.getGameStatus());
        Assertions.assertEquals(0, response.getPit(PLAYER1_SIXTH_HOUSE).getSeeds());
        Assertions.assertEquals(0, response.getPit(PLAYER1_THIRD_HOUSE).getSeeds());
        Assertions.assertEquals(0, response.getPit(PLAYER2_FOURTH_HOUSE).getSeeds());
        Assertions.assertEquals(13, response.getPit(PLAYER1_STORE).getSeeds());
    }

    @Test
    void sowSeeds_EndGame() {
        Mockito.when(kalahGameRepository.save(Mockito.any())).thenReturn(kalahGame);

        kalahGame = new KalahGame(PlayerEnum.PLAYER_SECOND);
        kalahGame.getPit(1).setSeeds(0);
        kalahGame.getPit(2).setSeeds(0);
        kalahGame.getPit(3).setSeeds(0);
        kalahGame.getPit(4).setSeeds(0);
        kalahGame.getPit(5).setSeeds(5);
        kalahGame.getPit(6).setSeeds(10);
        kalahGame.getPit(7).setSeeds(28);
        kalahGame.getPit(8).setSeeds(0);
        kalahGame.getPit(9).setSeeds(0);
        kalahGame.getPit(10).setSeeds(0);
        kalahGame.getPit(11).setSeeds(0);
        kalahGame.getPit(12).setSeeds(0);
        kalahGame.getPit(13).setSeeds(1);
        kalahGame.getPit(14).setSeeds(28);

        KalahGame response = subjectUnderTest.sowSeeds(kalahGame,13);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(STATUS_END, response.getGameStatus());
        Assertions.assertEquals(PlayerEnum.PLAYER_SECOND.toString(), response.getGameWinner());
        Assertions.assertEquals(0, response.getPit(PLAYER1_SIXTH_HOUSE).getSeeds());
        Assertions.assertEquals(0, response.getPit(PLAYER1_FIFTH_HOUSE).getSeeds());
        Assertions.assertEquals(0, response.getPit(PLAYER2_SIXTH_HOUSE).getSeeds());
        Assertions.assertEquals(44, response.getPit(PLAYER2_STORE).getSeeds());
    }
}