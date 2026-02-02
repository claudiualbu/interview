package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.controller.support.ApiTestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.interview.common.correlation.CorrelationId.HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceLineItemControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void invoice_withTwoItems_listReturnsTwo_withCorrectTotals() throws Exception {
        String cid = "it-li-list";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);

        long roId = api.createRepairOrderAndReturnId("LI Demo", "VIN-LI-1", cid);
        long invoiceId = api.createInvoiceAndReturnId(roId, cid);

        long item1Id = api.createLineItemAndReturnId(invoiceId, "Labor", 1, 10000, cid);
        long item2Id = api.createLineItemAndReturnId(invoiceId, "Parts", 2, 2500, cid);

        mockMvc.perform(get("/api/v1/invoices/{invoiceId}/line-items", invoiceId)
                        .header(HEADER, cid))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value((int) item1Id))
                .andExpect(jsonPath("$[0].invoiceId").value((int) invoiceId))
                .andExpect(jsonPath("$[0].description").value("Labor"))
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[0].unitPriceCents").value(10000))
                .andExpect(jsonPath("$[0].lineTotalCents").value(10000))
                .andExpect(jsonPath("$[1].id").value((int) item2Id))
                .andExpect(jsonPath("$[1].description").value("Parts"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].unitPriceCents").value(2500))
                .andExpect(jsonPath("$[1].lineTotalCents").value(5000));
    }

    @Test
    void updateThenDelete_item_changesList() throws Exception {
        String cid = "it-li-update-delete";
        ApiTestClient api = new ApiTestClient(mockMvc, objectMapper);

        long roId = api.createRepairOrderAndReturnId("LI Demo 2", "VIN-LI-2", cid);
        long invoiceId = api.createInvoiceAndReturnId(roId, cid);

        long itemId = api.createLineItemAndReturnId(invoiceId, "Labor", 1, 10000, cid);
        api.createLineItemAndReturnId(invoiceId, "Parts", 1, 2500, cid);

        mockMvc.perform(put("/api/v1/line-items/{itemId}", itemId)
                        .header(HEADER, cid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description": "Labor (updated)",
                              "quantity": 2,
                              "unitPriceCents": 12000
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(header().string(HEADER, cid))
                .andExpect(jsonPath("$.id").value((int) itemId))
                .andExpect(jsonPath("$.description").value("Labor (updated)"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.unitPriceCents").value(12000))
                .andExpect(jsonPath("$.lineTotalCents").value(24000));

        mockMvc.perform(delete("/api/v1/line-items/{itemId}", itemId)
                        .header(HEADER, cid))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HEADER, cid));

        mockMvc.perform(get("/api/v1/invoices/{invoiceId}/line-items", invoiceId)
                        .header(HEADER, cid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Parts"));
    }
}
