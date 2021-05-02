package org.parchmentmc.axe.common;

public interface Constants {

    String MINECRAFT_RELEASE_TRIGGER_NAME = "minecraftReleaseTrigger";
    String MINECRAFT_RELEASE_TRIGGER_DISPLAYNAME = "Minecraft Release";
    String MINECRAFT_RELEASE_TRIGGER_DESCRIPTION = "Triggers a build when ever Mojang releases a new release version of Minecraft";

    String MINECRAFT_SNAPSHOT_TRIGGER_NAME = "minecraftSnapshotTrigger";
    String MINECRAFT_SNAPSHOT_TRIGGER_DISPLAYNAME = "Minecraft Snapshot";
    String MINECRAFT_SNAPSHOT_TRIGGER_DESCRIPTION = "Triggers a build when ever Mojang releases a new snapshot version of Minecraft";

    String MINECRAFT_TRIGGER_NAME = "minecraftTrigger";
    String MINECRAFT_TRIGGER_DISPLAYNAME = "Minecraft Snapshot or Release";
    String MINECRAFT_TRIGGER_DESCRIPTION = "Triggers a build when ever Mojang releases a new version of Minecraft";


    String MINECRAFT_RELEASE_OR_SNAPSHOT_POLICY_LAST_VERSION_KEY = "lastVersion";

    String MINECRAFT_RELEASE_VERSIONS_MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
}
