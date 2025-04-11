package com.oscarbaltasar.rpchat.Data;

import net.minecraft.nbt.CompoundTag;

public class PlayerRPData {
    private String characterName = null;
    private boolean inCharacter = false;
    private boolean globalChat = true;
    private boolean listeningToGlobal = true;
    private String charColor = "ffffff";

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String name) {
        this.characterName = name;
    }

    public boolean isInCharacter() {
        return inCharacter;
    }

    public void toggleInCharacter() {
        this.inCharacter = !this.inCharacter;
    }

    public boolean isGlobalChat() {
        return globalChat;
    }

    public void toggleGlobalChat() {
        this.globalChat = !this.globalChat;
    }

    public boolean isListeningToGlobal() {
        return listeningToGlobal;
    }

    public void toggleListeningToGlobal() {
        this.listeningToGlobal = !this.listeningToGlobal;
    }

    public String getCharColor() {
        return charColor;
    }

    public void setCharColor(String color) {
        this.charColor = color;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (characterName != null) tag.putString("CharacterName", characterName);
        tag.putBoolean("InCharacter", inCharacter);
        tag.putBoolean("GlobalChat", globalChat);
        tag.putBoolean("ListeningToGlobal", listeningToGlobal);
        tag.putString("CharColor", this.charColor);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("CharacterName")) characterName = tag.getString("CharacterName");
        inCharacter = tag.getBoolean("InCharacter");
        globalChat = tag.getBoolean("GlobalChat");
        listeningToGlobal = tag.getBoolean("ListeningToGlobal");
        if (tag.contains("CharColor")) charColor = tag.getString("CharColor");
    }
}
