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

package com.backbase.kalah.integration;

import com.backbase.kalah.KalahApplication;
import com.backbase.kalah.constants.KalahConstants;
import com.backbase.kalah.model.PlayerEnum;
import com.backbase.kalah.response.KalahGameResponse;
import com.backbase.kalah.response.KalahSowResponse;
import com.backbase.kalah.response.KalahStatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = KalahApplication.class)
@AutoConfigureMockMvc
@DirtiesContext
@Slf4j
class KalahIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void playKalahGame() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .post("/games")
                    .contentType("application/json")
                    .param("player", PlayerEnum.PLAYER_FIRST.toString()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.uri").exists())
                .andReturn();

        KalahGameResponse kalahGameResponse = convertToKalahGameResponse(mvcResult.getResponse().getContentAsString());
        log.info("After Kalah Game Start - GameId: {}", kalahGameResponse.getId());

         mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .put("/games/{gameId}/pits/{pitId}", kalahGameResponse.getId(), 1)
                    .contentType("application/json"))
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(kalahGameResponse.getId()))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.uri").exists())
                 .andReturn();

        KalahSowResponse kalahSowResponse = convertToKalahSowResponse(mvcResult.getResponse().getContentAsString());
        log.info("Kalah Game After first Sow: {}", kalahSowResponse);

        mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .put("/games/{gameId}/pits/{pitId}", kalahGameResponse.getId(), 3)
                    .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(kalahGameResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uri").exists())
                .andReturn();

        kalahSowResponse = convertToKalahSowResponse(mvcResult.getResponse().getContentAsString());
        log.info("Kalah Game After second Sow: {}", kalahSowResponse);

        mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .get("/games/{gameId}", kalahGameResponse.getId())
                    .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(kalahGameResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameStatus").value(KalahConstants.STATUS_ON))
                .andReturn();

        KalahStatusResponse kalahStatusResponse = convertToKalahStatusResponse(mvcResult.getResponse().getContentAsString());
        log.info("Kalah Game After get status: {}", kalahStatusResponse);

    }

    private KalahGameResponse convertToKalahGameResponse(String contentAsString) {
        KalahGameResponse kalahGameResponse = null;
        try {
            kalahGameResponse = objectMapper.readValue(contentAsString, KalahGameResponse.class);
        } catch (JsonProcessingException ex) {
            log.error("Exception while converting json to object: {}", ex.getMessage());
        }
        return kalahGameResponse;
    }

    private KalahSowResponse convertToKalahSowResponse(String contentAsString) {
        KalahSowResponse kalahSowResponse = null;
        try {
            kalahSowResponse = objectMapper.readValue(contentAsString, KalahSowResponse.class);
        } catch (JsonProcessingException ex) {
            log.error("Exception while converting json to object: {}", ex.getMessage());
        }
        return kalahSowResponse;
    }

    private KalahStatusResponse convertToKalahStatusResponse(String contentAsString) {
        KalahStatusResponse kalahStatusResponse = null;
        try {
            kalahStatusResponse = objectMapper.readValue(contentAsString, KalahStatusResponse.class);
        } catch (JsonProcessingException ex) {
            log.error("Exception while converting json to object: {}", ex.getMessage());
        }
        return kalahStatusResponse;
    }

}
