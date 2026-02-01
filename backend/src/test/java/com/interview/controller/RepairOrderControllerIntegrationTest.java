package com.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class RepairOrderControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create_get_update_delete_happyFlow() throws Exception {
        long id = createRepairOrderAndReturnId("John Doe", "VIN-123");

        mockMvc.perform(get("/api/v1/repair-orders/{id}", id))
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        mockMvc.perform(put("/api/v1/repair-orders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "customerName": "John Doe Updated",
                    "vehicleVin": "VIN-999",
                    "status": "CLOSED"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe Updated"))
                .andExpect(jsonPath("$.vehicleVin").value("VIN-999"))
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        mockMvc.perform(delete("/api/v1/repair-orders/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/repair-orders/", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_containsCreatedRepairOrder() throws Exception {
        long id = createRepairOrderAndReturnId("Jane Doe", "VIN-ABC");

        mockMvc.perform(get("/api/v1/repair-orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id", hasItem((int) id)));
    }

    @Test
    void create_invalidPayload_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/repair-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "customerName": "",
                              "vehicleVin": ""
                            }
                            """))
                .andExpect(status().isBadRequest());

    }

    @Test
    void get_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/repair-orders/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    private long createRepairOrderAndReturnId(String customerName, String vin) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/repair-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerName":  "%s",
                      "vehicleVin":  "%s"
                    }""".formatted(customerName, vin)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
