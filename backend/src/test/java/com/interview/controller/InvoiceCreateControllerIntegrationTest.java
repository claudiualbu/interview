package com.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.controller.support.ApiTestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.interview.common.correlation.CorrelationId.HEADER;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceCreateControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createInvoice_forExistingRepairOrder_returns201_andInvoice() throws Exception {
        String cid = "it-inv-201";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);
        long roId = api.createRepairOrderAndReturnId("Invoice Demo", "VIN-INV-1", cid);

        MvcResult result = mockMvc.perform(post("/api/v1/invoices")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "repairOrderId": %d }
                            """.formatted(roId)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.repairOrderId").value((int) roId))
                .andExpect(jsonPath("$.invoiceNumber", notNullValue()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        long invoiceId = json.get("id").asLong();

        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repairOrderId\": %d}".formatted(roId)))
                .andExpect(status().isConflict());
    }

    @Test
    void createInvoice_forUnknownRepairOrder_returns404_problemDetailIncludesCorrelationId() throws Exception {
        String cid = "it-inv-404";

        mockMvc.perform(post("/api/v1/invoices")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "repairOrderId": 999999 }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.correlationId").value(cid));
    }

    @Test
    void createInvoice_twiceForSameRepairOrder_returns409_problemDetailIncludesCorrelationId() throws Exception {
        String cid = "it-inv-409";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);
        long roId = api.createRepairOrderAndReturnId("Dup Demo", "VIN-DUP-1", cid);

        mockMvc.perform(post("/api/v1/invoices")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "repairOrderId": %d }
                            """.formatted(roId)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HEADER, cid));

        mockMvc.perform(post("/api/v1/invoices")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "repairOrderId": %d }
                            """.formatted(roId)))
                .andExpect(status().isConflict())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.correlationId").value(cid));
    }
}
