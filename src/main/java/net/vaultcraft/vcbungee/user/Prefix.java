package net.vaultcraft.vcbungee.user;

/**
 * Created by Connor on 7/20/14. Designed for the VCUtils project.
 */

public enum Prefix {

    VAULT_CRAFT("&5&lV&7&lC&f: &7", " &f&l", "&7"),
    ERROR("&4&lERROR&f: &7", "", "&7"),
    WARNING("&e&lWARNING&f: &7", "", "&7"),
    SUCCESS("&a&lSUCCESS&f: &7", "", "&7"),
    CHARACTER("&6&l{0}&7: &f", "", "&f"),
    NOTHING("", "", "");

    private String prefix;
    private String suffix;
    private String chatColor;

    Prefix(String prefix, String suffix, String chatColor) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.chatColor = chatColor;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getChatColor() {
        return chatColor;
    }

    public String getSuffix() {
        return suffix;
    }
}
