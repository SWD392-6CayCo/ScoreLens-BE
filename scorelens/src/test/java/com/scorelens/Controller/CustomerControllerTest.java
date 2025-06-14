package com.scorelens.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.scorelens.Controller.v1.CustomerV1Controller;
import com.scorelens.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(CustomerV1Controller.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void testCreateCustomer() throws Exception {
//        CustomerCreateRequestDto request = new CustomerCreateRequestDto("John Doe", "john@example.com", "09090808", "123456", LocalDate.of(2004, 8,18));
//        CustomerResponseDto response = new CustomerResponseDto(null, "John Doe", "john@example.com","09090808", LocalDate.of(2004, 8,18), LocalDate.now(), null, "normal", "active");
//
//        Mockito.when(customerService.createCustomer(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/customers")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andExpect(jsonPath("$.email").value("john@example.com"));
//    }
//
//    @Test
//    void testGetAllCustomers() throws Exception {
//        List<CustomerResponseDto> customers = List.of(
//                new CustomerResponseDto("007", "tuyendepchai", "tuyen@gmail.com", "0987654321", LocalDate.of(2004, 9, 18), LocalDate.of(2004, 6, 2), LocalDate.of(2004, 6, 2), "vip", "inactive"),
//                new CustomerResponseDto("a511d73c-a612-4110-b45c-a0de805fe82e", "Nguyễn Văn A", "nguyenvana@gmail.com", "09876543", LocalDate.of(2004, 2, 18), LocalDate.of(2004, 6, 2), null, "normal", "active")
//        );
//
//        Mockito.when(customerService.findAll()).thenReturn(customers);
//
//        mockMvc.perform(get("/api/customers"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2))
//                .andExpect(jsonPath("$[0].name").value("Alice"))
//                .andExpect(jsonPath("$[1].name").value("Bob"));
//    }
//
//    @Test
//    void testGetCustomerById() throws Exception {
//        CustomerResponseDto customer = new CustomerResponseDto("a511d73c-a612-4110-b45c-a0de805fe82e", "Nguyễn Văn A", "nguyenvana@gmail.com", "09876543", LocalDate.of(2004, 2, 18), LocalDate.of(2004, 6, 2), null, "normal", "active");
//
//        Mockito.when(customerService.findById("fd4b2af5-1182-4641-b9d3-d7fc151222bb")).thenReturn(customer);
//
//        mockMvc.perform(get("/api/customers/fd4b2af5-1182-4641-b9d3-d7fc151222bb"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Nguyễn Văn A"));
//    }
//
////    @Test
////    void testUpdateCustomer() throws Exception {
////        CustomerCreateRequestDto request = new CustomerCreateRequestDto("Updated", "updated@example.com");
////        CustomerResponseDto response = new CustomerResponseDto(1L, "Updated", "updated@example.com");
////
////        Mockito.when(customerService.updateCustomer(any(), any())).thenReturn(response);
////
////        mockMvc.perform(put("/api/customers/1")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(request)))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.name").value("Updated"));
////    }
//
//    @Test
//    void testDeleteCustomer() throws Exception {
//        Mockito.when(customerService.deleteById("007")).thenReturn(true); // Mock trả về true khi xóa thành công
//
//        mockMvc.perform(delete("/api/customers/007").with(csrf()))
//                .andExpect(status().isNoContent()); // Vẫn giữ .isNoContent() nếu controller trả về 204 dù service trả về boolean
//    }
}
