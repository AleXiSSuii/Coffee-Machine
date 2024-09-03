package com.coffeeMachine.service;

import com.coffeeMachine.dto.request.OrderRequest;

public interface OrderService {

    void orderDrink(OrderRequest request);
    void deleteAllOrdersOlderFiveYears();
}
