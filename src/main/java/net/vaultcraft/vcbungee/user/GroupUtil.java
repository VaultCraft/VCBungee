package net.vaultcraft.vcbungee.user;

import java.util.List;

/**
 * Created by Connor on 9/21/14. Designed for the vcbungee project.
 */

public class GroupUtil {

    public enum Group {
        OWNER(14, false),
        DEVELOPER(13, false),
        MANAGER(12, false),
        ADMIN(11, false),
        MOD(10, false),
        HELPER(9, false),
        ENDERDRAGON(8, true),
        WITHER(7, true),
        ENDERMAN(6, true),
        YOUTUBE(5, true),
        SKELETON(4, true),
        SLIME(3, true),
        WOLF(2, true),
        COMMON(1, false);

        private int perm;
        private boolean isDonorRank;

        private Group(int perm, boolean isDonorRank) {
            this.perm = perm;
        }

        public int getPermissionLevel() {
            return perm;
        }

        private boolean hasPermission(Group me, Group other) {
            int level = other.perm;
            boolean donor = other.isDonorRank;

            if (me.perm >= 10)
                return true;

            if (donor) {
                return (me.perm >= level && me.isDonorRank);
            } else {
                return (me.perm >= level);
            }
        }

        public static Group fromPermLevel(int permLevel) {
            for(Group g: values()) {
                if(g.perm == permLevel)
                    return g;
            }

            return null;
        }
    }

    public static boolean hasPermission(List<Integer> groups, Group group) {
        for (int i : groups) {
            Group g = Group.fromPermLevel(i);
            if (g.hasPermission(g, group))
                return true;
        }

        return false;
    }
}
