package com.audrina.eccommerce.order;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderItemResponse toOrderResponse(OrderItem orderItem) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setProductId(orderItem.getProductId());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        return orderItemResponse;

    }

    public OrderResponse toOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setUserId(order.getUserId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setStatus(order.getStatus());

        List<OrderItemResponse> orderItemList = order.getOrderItemsList().stream().map(
                this::toOrderResponse
        ).toList();

        orderResponse.setOrderItemResponseList(orderItemList);
        return orderResponse;
    }

}
