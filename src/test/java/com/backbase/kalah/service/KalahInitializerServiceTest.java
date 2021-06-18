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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class KalahInitializerServiceTest {

    private KalahInitializerService subjectUnderTest;

    @Mock
    private KalahGameRepository kalahGameRepository;

    @BeforeEach
    void setUp() {
        subjectUnderTest = new KalahInitializerService(kalahGameRepository);
    }

    @Test
    void initAndStartGame() {
        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        Mockito
                .when(kalahGameRepository.save(Mockito.any()))
                .thenReturn(kalahGame);
        KalahGame response = subjectUnderTest.initAndStartGame(PlayerEnum.PLAYER_FIRST.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST, kalahGame.getPlayer());
        Assertions.assertEquals(14,response.getPits().size());
    }

    @Test
    void getGameById() {
        Mockito
                .when(kalahGameRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());

        KalahException kalahException = Assertions.assertThrows(KalahException.class, () -> subjectUnderTest.getGameById("G1001"));
        Assertions.assertEquals(ErrorTypeEnum.KGE_01.getErrorCode(), kalahException.getErrorCode());

        KalahGame kalahGame = new KalahGame(PlayerEnum.PLAYER_FIRST);
        Mockito
                .when(kalahGameRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(kalahGame));
        KalahGame response = subjectUnderTest.getGameById("G1001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PlayerEnum.PLAYER_FIRST, kalahGame.getPlayer());
        Assertions.assertEquals(14,response.getPits().size());
    }
}