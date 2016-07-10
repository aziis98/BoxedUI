package com.aziis98.boxed.events

import java.util.*

// Copyright 2016 Antonio De Lucreziis

class EventDispatcher {

    val listeners = HashMap<String, LinkedList<(Any) -> EventResult>>()

    var afterDispatchAction: (String) -> Unit = { }

    var parent: EventDispatcher? = null

    fun on(name: String, listener: () -> Unit) {
        on<Any>(name, { data -> listener() })
    }

    fun <R> on(name: String, listener: (R) -> Unit) {
        val action: (R) -> EventResult = { data ->
            listener(data)
            EventResult.Continue
        }

        onCancellable(name, action)
    }

    fun onCancellable(name: String, listener: () -> EventResult) {
        onCancellable<Any>(name, { data -> listener() })
    }

    /**
     * @param name Name of the event
     * @param listener Called when the event is dispatched, to stop the listener chain return true
     */
    @Suppress("UNCHECKED_CAST")
    fun <R> onCancellable(name: String, listener: (R) -> EventResult) {
        var list = listeners[name]
        if (list == null) {
            list = LinkedList()
            listeners[name] = list
        }
        list.add({ data -> listener(data as R) })
    }

    fun broadcast(name: String, data: Any = NO_DATA) {
        parent?.broadcast(name, data)

        dispatch(name, data)
    }

    fun dispatch(name: String, data: Any = NO_DATA) {
        val find = listeners[name]?.find { it(data) == EventResult.Cancel }
        if (find == null) {
            afterDispatchAction(name)
        }
    }

    companion object {
        val NO_DATA = Any()
    }

}

enum class EventResult {
    Continue, Cancel
}
