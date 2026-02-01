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

import static com.interview.common.correlation.CorrelationId.HEADER;
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
        String cid = "it-ro-crud";
        long id = createRepairOrderAndReturnId("John Doe", "VIN-123", cid);

        mockMvc.perform(get("/api/v1/repair-orders/{id}", id).header(HEADER, cid))
                .andExpect((status().isOk()))
                .andExpect(header().string(HEADER, cid))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        mockMvc.perform(put("/api/v1/repair-orders/{id}", id).header(HEADER, cid)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "customerName": "John Doe Updated",
                    "vehicleVin": "VIN-999",
                    "status": "CLOSED"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(jsonPath("$.customerName").value("John Doe Updated"))
                .andExpect(jsonPath("$.vehicleVin").value("VIN-999"))
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        mockMvc.perform(delete("/api/v1/repair-orders/{id}", id).header(HEADER, cid))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HEADER, cid));

        mockMvc.perform(get("/api/v1/repair-orders/{id}", id).header(HEADER, cid))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.correlationId").value(cid));
    }

    @Test
    void list_containsCreatedRepairOrder() throws Exception {
        String cid = "it-ro-list";
        long id = createRepairOrderAndReturnId("Jane Doe", "VIN-ABC", cid);

        mockMvc.perform(get("/api/v1/repair-orders").header(HEADER, cid))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id", hasItem((int) id)));
    }

    @Test
    void create_invalidPayload_returns400_withProblemDetailAndCorrelationId() throws Exception {
        String cid = "it-ro-400";

        mockMvc.perform(post("/api/v1/repair-orders")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "customerName": "",
                              "vehicleVin": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.correlationId").value(cid))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }

    @Test
    void get_unknownId_returns404_withProblemDetailAndCorrelationId() throws Exception {
        String cid = "it-ro-404";

        mockMvc.perform(get("/api/v1/repair-orders/{id}", 999999).header(HEADER, cid))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.correlationId").value(cid));
    }

    private long createRepairOrderAndReturnId(String customerName, String vin, String correlationId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/repair-orders")
                .header(HEADER, correlationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerName":  "%s",
                      "vehicleVin":  "%s"
                    }""".formatted(customerName, vin)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HEADER, correlationId))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
