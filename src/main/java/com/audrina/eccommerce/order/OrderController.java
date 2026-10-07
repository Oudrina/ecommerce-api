package com.audrina.eccommerce.order;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.PartitionKey;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private  final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponse> purchase( @PathVariable Long userId){
       return  new ResponseEntity<>(  orderService.saveOrder(userId), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public  ResponseEntity<OrderResponse>  getOrder(@PathVariable Long orderId){
        return new ResponseEntity<>(orderService.getOderById(orderId),  HttpStatus.OK);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<Page<OrderResponse>> getUserOrders( @PathVariable Long userId,
                                                             @RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "10")int pageSize){
        return  new ResponseEntity<>( orderService.getOrdersByUser(userId, pageNumber,pageSize), HttpStatus.OK);
    }

    @GetMapping
    public  ResponseEntity<Page<OrderResponse>> getAllOrders(@RequestParam(defaultValue = "0")int pageSize,
                                                             @RequestParam(defaultValue = "10") int pageNumber){
        return  new ResponseEntity<>(orderService.getAllOrders(pageNumber,pageSize), HttpStatus.OK);
    }



}
