package dev.imanuel.jewels.utils

fun <T> List<T>.insertSortedBy(
    element: T,
    comparator: Comparator<T>
): List<T> {
    val index = binarySearch(element, comparator).let { if (it >= 0) it else -it - 1 }
    return toMutableList().apply { add(index, element) }
}