package com.ark.commerce.ui

import com.ark.commerce.application.OrderPlaceRequest
import com.ark.commerce.application.OrderResponse
import com.ark.commerce.application.OrderService
import com.ark.base.common.CurrentUser
import com.ark.base.ui.auth.AccessType
import com.ark.base.ui.auth.Authorize
import com.ark.base.ui.auth.InjectCurrentUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun place(
        @RequestBody request: OrderPlaceRequest,
        @InjectCurrentUser buyer: CurrentUser,
    ): OrderResponse = orderService.place(request, buyer.requireUserId())

    @Authorize(AccessType.ORDER_BUYER, param = "orderId")
    @PatchMapping("/{orderId}/cancel")
    fun cancel(
        @PathVariable orderId: Long,
    ): OrderResponse = orderService.cancel(orderId)

    @Authorize(AccessType.ORDER_SELLER, param = "orderId")
    @PatchMapping("/{orderId}/confirm")
    fun confirm(
        @PathVariable orderId: Long,
    ): OrderResponse = orderService.confirm(orderId)

    @Authorize(AccessType.ORDER_SELLER, param = "orderId")
    @PatchMapping("/{orderId}/ship")
    fun ship(
        @PathVariable orderId: Long,
    ): OrderResponse = orderService.ship(orderId)

    @Authorize(AccessType.ORDER_SELLER, param = "orderId")
    @PatchMapping("/{orderId}/deliver")
    fun deliver(
        @PathVariable orderId: Long,
    ): OrderResponse = orderService.deliver(orderId)
}
