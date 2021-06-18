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

package com.backbase.kalah.utils;

import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.model.KalahGame;
import com.backbase.kalah.response.KalahGameResponse;
import com.backbase.kalah.response.KalahSowResponse;
import com.backbase.kalah.response.KalahStatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransformUtils {

    private TransformUtils() {
    }

    /**
     * converts KalahGame to KalahGameResponse
     * @param kalahGame - Object that is saved in mongo
     * @return KalahGameResponse - response sent back to client
     */
    public static KalahGameResponse getKalahGameResponse(KalahGame kalahGame) {
        KalahGameResponse kalahGameResponse = new KalahGameResponse();
        kalahGameResponse.setId(kalahGame.getId());
        kalahGameResponse.setUri(getUri(kalahGame.getId()));
        return kalahGameResponse;
    }

    /**
     * converts KalahGame to KalahSowResponse
     * @param kalahGame - Object that is saved in mongo
     * @return KalahSowResponse - response sent back to client
     */
    public static KalahSowResponse getKalahSowResponse(KalahGame kalahGame) {
        KalahSowResponse kalahSowResponse = new KalahSowResponse();
        kalahSowResponse.setId(kalahGame.getId());
        kalahSowResponse.setUri(getUri(kalahGame.getId()));
        Map<String,String> status = kalahGame.getPits().stream()
                .collect(Collectors.toMap(pit -> String.valueOf(pit.getIndex()),
                        pit -> String.valueOf(pit.getSeeds())));
        kalahSowResponse.setStatus(status);
        return kalahSowResponse;
    }

    /**
     * converts KalahGame to KalahSowResponse
     * @param kalahGame - Object that is saved in mongo
     * @return KalahStatusResponse - response sent back to client with game status and winner
     */
    public static KalahStatusResponse getKalahStatusResponse(KalahGame kalahGame) {
        KalahStatusResponse kalahStatusResponse = new KalahStatusResponse();
        kalahStatusResponse.setId(kalahGame.getId());
        kalahStatusResponse.setUri(getUri(kalahGame.getId()));
        kalahStatusResponse.setGameStatus(kalahGame.getGameStatus());
        kalahStatusResponse.setGameWinner(kalahGame.getGameWinner());
        return kalahStatusResponse;
    }


    private static String getUri(String id) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        if(StringUtils.isBlank(baseUrl)){
            baseUrl = KalahConstants.DEFAULT_BASE_URL;
        }
        return baseUrl + KalahConstants.BASE_PATH + id;
    }

}
