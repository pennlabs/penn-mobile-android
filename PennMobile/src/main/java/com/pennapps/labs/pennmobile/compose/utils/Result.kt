package com.pennapps.labs.pennmobile.compose.utils

/**
 * A generic sealed class that represents the result of an asynchronous operation,
 * typically a network request. It can either be a [Success] or an [Error].
 *
 * @param T The type of the successful data.
 */
sealed class Result<out T> {
    /**
     * Represents a successful result of an operation.
     * @param data The data returned by the successful operation.
     */
    data class Success<out T>(
        val data: T,
    ) : Result<T>()

    /**
     * Represents a failed result of an operation.
     * @param message A user-friendly message describing the error.
     * @param cause An optional exception that caused the error, for logging and debugging.
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ) : Result<Nothing>()

    val isSuccessful: Boolean
        get() = this is Success
}
