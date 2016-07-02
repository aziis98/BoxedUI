package com.aziis98.boxed.events

import java.util.*

// Copyright 2016 Antonio De Lucreziis

class EventDispatcher {

    val listeners = HashMap<String, LinkedList<() -> EventResult>>()

    var afterDispatchAction: (String) -> Unit = { }

    var parent: EventDispatcher? = null

    fun on(name: String, listener: () -> Unit) {
        val action: () -> EventResult = {
            listener()
            EventResult.Continue
        }

        onCancellable(name, action)
    }

    /**
     * @param name Name of the event
     * @param listener Called when the event is dispatched, to stop the listener chain return true
     */
    fun onCancellable(name: String, listener: () -> EventResult) {
        var list = listeners[name]
        if (list == null) {
            list = LinkedList()
            listeners[name] = list
        }
        list.add(listener)
    }

    fun broadcast(name: String) {
        parent?.broadcast(name)

        dispatch(name)
    }

    fun dispatch(name: String) {
        val find = listeners[name]?.find { it() == EventResult.Cancel }
        if (find == null) {
            afterDispatchAction(name)
        }
    }

}

enum class EventResult  {
    Continue, Cancel
}
