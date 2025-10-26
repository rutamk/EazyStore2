package com.eazybytes.eazystore.controller;

import com.eazybytes.eazystore.constants.ApplicationConstants;
import com.eazybytes.eazystore.dto.ContactResponseDto;
import com.eazybytes.eazystore.dto.OrderResponseDto;
import com.eazybytes.eazystore.dto.ResponseDto;
import com.eazybytes.eazystore.service.IContactService;
import com.eazybytes.eazystore.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IOrderService iOrderService;
    private final IContactService iContactService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllPendingOrders() {
        return ResponseEntity.ok().body(iOrderService.getAllPendingOrders());
    }

    // Using JPQL query with void return type for direct DML operation
    @PatchMapping("/orders/{orderId}/confirm")
    public ResponseEntity<ResponseDto> confirmOrder(@PathVariable Long orderId){

        iOrderService.updateOrderStatus(orderId, ApplicationConstants.ORDER_STATUS_CONFIRMED);
        return ResponseEntity.ok(new ResponseDto("200","Order #" + orderId
        + " has been approved.")
        );
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ResponseDto> cancelOrder(@PathVariable Long orderId){

        iOrderService.updateOrderStatus(orderId, ApplicationConstants.ORDER_STATUS_CANCELLED);
        return ResponseEntity.ok(new ResponseDto("200","Order #" + orderId
                + " has been cancelled.")
        );
    }

    // Using JPA QUERY WITH RETURN TYPE AS Order (commented in IOrderService)
//    @PatchMapping("/orders/{orderId}/confirm")
//    public ResponseEntity<ResponseDto> confirmOrder(@PathVariable Long orderId){
//
//        Order confirmedOrder = iOrderService.updateOrderStatus(orderId, ApplicationConstants.ORDER_STATUS_CONFIRMED);
//        return ResponseEntity.ok(new ResponseDto("200","Order #" + confirmedOrder.getOrderId()
//        + " has been approved.")
//        );
//    }
//
//    @PatchMapping("/orders/{orderId}/cancel")
//    public ResponseEntity<ResponseDto> cancelOrder(@PathVariable Long orderId){
//
//        Order cancelledOrder = iOrderService.updateOrderStatus(orderId, ApplicationConstants.ORDER_STATUS_CANCELLED);
//        return ResponseEntity.ok(new ResponseDto("200","Order #" + cancelledOrder.getOrderId()
//                + " has been cancelled.")
//        );
//    }

    @GetMapping("/messages")
    public ResponseEntity<List<ContactResponseDto>> getAllOpenMessages() {
        return ResponseEntity.ok(iContactService.getAllOpenMessages());
    }

    @PatchMapping("/messages/{contactId}/close")
    public ResponseEntity<ResponseDto> closeMessage(@PathVariable Long contactId){
        iContactService.updateMessageStatus(contactId,ApplicationConstants.CLOSED_MESSAGE);
        return ResponseEntity.ok(
                new ResponseDto("200","Contact #" + contactId + " has been closed.")
        );
    }

}
