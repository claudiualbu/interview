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

    public long createInvoiceAndReturnId(long repairOrderId, String cid) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/invoices")
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "repairOrderId": %d }
                            """.formatted(repairOrderId)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    public void createLineItem(long invoiceId, String description, int qty, int unitPriceCents, String cid)
            throws Exception {
        mockMvc.perform(post("/api/v1/invoices/{invoiceId}/line-items", invoiceId)
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description": "%s",
                              "quantity": %d,
                              "unitPriceCents": %d
                            }
                            """.formatted(description, qty, unitPriceCents)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HEADER, cid));
    }

    public long createLineItemAndReturnId(long invoiceId, String description, int qty, int unitPrice, String cid)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/invoices/{invoiceId}/line-items", invoiceId)
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description": "%s",
                              "quantity": %d,
                              "unitPriceCents": %d
                            }
                            """.formatted(description, qty, unitPrice)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HEADER, cid))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    public JsonNode findInvoiceById(JsonNode listArray, long invoiceId) {
        if (listArray == null || !listArray.isArray()) {
            return null;
        }
        for (JsonNode node : listArray) {
            if (node.hasNonNull("id") && node.get("id").asLong() == invoiceId) {
                return node;
            }
        }
        return null;
    }

    public void issueInvoice(long invoiceId, String cid) throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/v1/invoices/{id}", invoiceId)
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "version": 0,
                          "status": "ISSUED"
                        }
                        """))
                .andExpect(status().isOk());
    }
}
