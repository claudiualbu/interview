package com.interview.controller.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.interview.common.correlation.CorrelationId.HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public long createRepairOrderAndReturnId(String customerName, String vin, String correlationId) throws Exception {
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
