package com.ark.base.common

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

fun afterTransactionCommit(action: () -> Unit) {
    TransactionSynchronizationManager.registerSynchronization(
        object : TransactionSynchronization {
            override fun afterCommit() = action()
        },
    )
}
