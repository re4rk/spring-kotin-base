package com.ark.base.ui.admin

data class LogStatusStat(
    val status: String,
    val count: Long,
    val badgeClass: String,
)

data class LogStats(
    val total: Long,
    val items: List<LogStatusStat>,
)

fun buildLogStats(
    statuses: Array<out Enum<*>>,
    grouped: List<Array<Any>>,
): LogStats {
    val counts =
        grouped.associate { row ->
            val status = row[0] as Enum<*>
            val count = row[1] as Long
            status.name to count
        }
    val items =
        statuses.map { status ->
            LogStatusStat(
                status = status.name,
                count = counts[status.name] ?: 0L,
                badgeClass = statusBadgeClass(status.name),
            )
        }
    return LogStats(
        total = items.sumOf { it.count },
        items = items,
    )
}

private fun statusBadgeClass(status: String): String =
    when (status) {
        "SUCCESS", "DELIVERED" -> "bg-success"
        "FAILED" -> "bg-danger"
        "PENDING" -> "bg-secondary"
        "BOUNCED", "MISSED" -> "bg-warning text-dark"
        else -> "bg-light text-dark"
    }
