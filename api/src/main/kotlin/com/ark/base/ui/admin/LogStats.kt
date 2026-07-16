package com.ark.base.ui.admin

data class LogStatusStat(
    val status: String,
    val count: Long,
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
            )
        }
    return LogStats(
        total = items.sumOf { it.count },
        items = items,
    )
}
