package com.gamota.youtubeplayer.event;

public class MessageEvent {
    public final boolean dataChanged;
    public MessageEvent(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }
}
