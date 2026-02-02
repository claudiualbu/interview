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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceViewsControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void invoiceList_includesCountAndTotal_forInvoiceWithTwoItems() throws Exception {
        String cid = "it-inv-list";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);

        long roId = api.createRepairOrderAndReturnId("List Demo", "VIN-LIST-1", cid);
        long invoiceId = api.createInvoiceAndReturnId(roId, cid);

        api.createLineItem(invoiceId, "Labor", 1, 10000, cid);
        api.createLineItem(invoiceId, "Parts", 2, 2500, cid);

        MvcResult listResult = mockMvc.perform(get("/api/v1/invoices").header(HEADER, cid))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode listJson = objectMapper.readTree(listResult.getResponse().getContentAsString());

        JsonNode invoiceNode = api.findInvoiceById(listJson, invoiceId);
        if (invoiceNode == null) {
            throw new AssertionError("Invoice not found in list response. invoiceId=" + invoiceId);
        }

        org.junit.jupiter.api.Assertions.assertEquals(invoiceId, invoiceNode.get("id").asLong());
        org.junit.jupiter.api.Assertions.assertEquals(roId, invoiceNode.get("repairOrderId").asLong());
        org.junit.jupiter.api.Assertions.assertNotNull(invoiceNode.get("invoiceNumber"));
        org.junit.jupiter.api.Assertions.assertEquals("DRAFT", invoiceNode.get("status").asText());
        org.junit.jupiter.api.Assertions.assertEquals(2, invoiceNode.get("lineItemCount").asInt());
        org.junit.jupiter.api.Assertions.assertEquals(15000L, invoiceNode.get("totalCents").asLong());
    }

    @Test
    void invoiceDetails_includesTwoItems_andAggregatesMatch() throws Exception {
        String cid = "it-inv-details";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);

        long roId = api.createRepairOrderAndReturnId("Details Demo", "VIN-DET-1", cid);
        long invoiceId = api.createInvoiceAndReturnId(roId, cid);

        long item1Id = api.createLineItemAndReturnId(invoiceId, "Labor", 1, 10000, cid);
        long item2Id = api.createLineItemAndReturnId(invoiceId, "Parts", 2, 2500, cid);

        mockMvc.perform(get("/api/v1/invoices/{id}/details", invoiceId).header(HEADER, cid))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").value((int) invoiceId))
                .andExpect(jsonPath("$.repairOrderId").value((int) roId))
                .andExpect(jsonPath("$.invoiceNumber", notNullValue()))
                .andExpect(jsonPath("$.status").value("DRAFT"))

                .andExpect(jsonPath("$.lineItemCount").value(2))
                .andExpect(jsonPath("$.totalCents").value(15000))

                .andExpect(jsonPath("$.lineItems.length()").value(2))
                .andExpect(jsonPath("$.lineItems[0].id").value((int) item1Id))
                .andExpect(jsonPath("$.lineItems[0].description").value("Labor"))
                .andExpect(jsonPath("$.lineItems[0].lineTotalCents").value(10000))
                .andExpect(jsonPath("$.lineItems[1].id").value((int) item2Id))
                .andExpect(jsonPath("$.lineItems[1].description").value("Parts"))
                .andExpect(jsonPath("$.lineItems[1].lineTotalCents").value(5000))

                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }
}
