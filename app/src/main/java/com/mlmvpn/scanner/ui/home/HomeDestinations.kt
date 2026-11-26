package com.mlmvpn.scanner.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mlmvpn.scanner.R

/**
 * One tile on the home screen.
 *
 * `opensAsModal` marks the destinations that are not tabs: the emergency screens, the Iran and
 * domain-fronting profiles and the anti-sanction DNS screen render as full-screen overlays above
 * everything, so they are
 * routed through AppScreen's `activeModal` rather than through its tab stack. Getting this wrong
 * shows a blank screen, which is why it is recorded here next to the id instead of being
 * re-derived at each call site.
 */
data class HomeApp(
    val id: String,
    val labelRes: Int,
    val icon: ImageVector,
    val tint: Color,
    val opensAsModal: Boolean = false,
    /**
     * Finished artwork for this icon, drawn instead of the glyph-on-a-tinted-tile.
     *
     * A drawable here already contains its own background and its own colours, so the tile does
     * NOT paint one behind it -- painting a tint under a complete icon would show as a coloured
     * ring around its corners. [icon] and [tint] stay filled in as the fallback for anywhere the
     * artwork is not appropriate, and so a destination is never left without a glyph.
     */
    val imageRes: Int? = null,
    /**
     * How much of the tile the glyph fills, as a fraction of the tile's side.
     *
     * A constant is nearly always right, which is why 0.52 is the default. It is wrong for the
     * few Material glyphs drawn small inside their own box -- Radar is concentric rings with a
     * wide margin baked in, so at the shared size it reads noticeably lighter than the solid
     * shapes beside it. This is an optical correction for those, not a knob to reach for.
     */
    val glyphScale: Float = 0.52f,
    /**
     * The desktop's own glyph for this destination (an `ic_glyph_*` vector generated from the
     * Windows sprite by scripts/icons-from-windows.js), drawn white on the tinted tile instead of
     * [icon]. [icon] stays as the fallback and for the places that want a Material glyph.
     */
    val glyphRes: Int? = null,
)

object HomeDestinations {

    // Apple's system colours. Using the real values rather than approximations is most of what
    // makes a grid of squircles read as "an iPhone home screen" instead of "coloured squares".
    val Blue   = Color(0xFF007AFF)
    val Green  = Color(0xFF34C759)
    val Indigo = Color(0xFF5856D6)
    val Orange = Color(0xFFFF9500)
    val Pink   = Color(0xFFFF2D55)
    val Purple = Color(0xFFAF52DE)
    val Red    = Color(0xFFFF3B30)
    val Teal   = Color(0xFF5AC8FA)
    val Yellow = Color(0xFFFFCC00)
    val Gray   = Color(0xFF8E8E93)

    /**
     * The dock. Fixed, not reorderable, and visible only on the home screen -- the same contract
     * as an iPhone's dock. Four is the whole point: it is the shortlist, so it does not grow.
     */
    val DOCK: List<HomeApp> = listOf(
        HomeApp("settings", R.string.home_settings, Icons.Default.Settings, Gray, imageRes = R.drawable.ic_app_settings),
    )

    /**
     * Destinations still being built (docs/URNETWORK-NPV-PLAN.md). They are on the board of a debug
     * build, where the owner tests them, and in no release until each one really connects -- a tile
     * that can only say «not yet» has no place on a user's phone. Remove an id here the day its
     * engine works. «URnetwork» and «NPV» left on 2026-10-20: both connect, and a release carries them.
     */
    val IN_PROGRESS: Set<String> = emptySet()

    val GRID_DEFAULT: List<HomeApp> = listOf(
        HomeApp("lantern", R.string.home_lantern, Icons.Default.Lightbulb, Teal, imageRes = R.drawable.ic_app_lantern),
        HomeApp("urnetwork", R.string.home_urnetwork, Icons.Default.Hub, Indigo, imageRes = R.drawable.ic_app_urnetwork),
    )

    /**
     * The folders a fresh board starts with, by key; members are destination ids, in order.
     *
     * «ابزارها» is the Windows desktop's folder of the same name: the destinations a user reaches
     * for now and then -- a fixed exit address, a subscription link, sharing with another device,
     * the usage meter, the tutorials -- rather than the ones that connect the phone. Like there, it
     * is only the folder a board starts with: it can be renamed, emptied and taken apart.
     */
    val DEFAULT_FOLDERS: Map<String, List<String>> = emptyMap()

    /**
     * A fresh board's top level: destination ids, and `@key` for a folder from [DEFAULT_FOLDERS].
     *
     * This decides a fresh install only; an existing board keeps its own order (see
     * [HomeLayoutStore]). Any destination missing here still appears -- the reconciliation
     * appends it -- so forgetting to list a new one costs its position, never its icon.
     */
    val BOARD_DEFAULT: List<String> = listOf("lantern", "urnetwork")

    /**
     * What a destination is for, which is what a folder made from it is called -- the way iOS
     * names a new folder after its apps' category instead of "New Folder". The category travels in
     * the folder's key ([newFolderKey]), so the name follows the app's language until the user
     * types one of their own, exactly as the factory «ابزارها» does.
     */
    enum class Category(val keyPart: String, val titleRes: Int) {
        CONNECT("connect", R.string.home_folder_connect),
        CONFIGS("configs", R.string.home_folder_configs),
        TOOLS("tools", R.string.home_folder_tools),
        GAMES("games", R.string.home_folder_games),
        EMERGENCY("emergency", R.string.home_folder_emergency),
    }

    fun categoryOf(id: String): Category? = when (id) {
        "masque", "wireguard", "warp_on_warp", "warp", "psiphon", "tor", "geph", "lantern", "urnetwork", "quick", "mae", "flux", "fenix",
        "amnezia", "proton", "windscribe", "outline", "npv", "github", "vpngate", "openvpn", "nodes" -> Category.CONNECT
        "freeconfig", "iran", "fronting", "sublink", "configstudio", "cloud" -> Category.CONFIGS
        "fixed_ip", "lan", "usage", "tutorial", "store", "scanner", "settings", "scout", "tgproxy", "widgetstudio" -> Category.TOOLS
        "game" -> Category.GAMES
        "emergency_2", "emergency_3", "antisanction" -> Category.EMERGENCY
        else -> null
    }

    /**
     * The key for a folder made by dropping [draggedId] onto [targetId]: their shared category, or
     * the target's -- the app that was there first is the one the folder grows out of.
     */
    fun newFolderKey(targetId: String, draggedId: String, now: Long = System.currentTimeMillis()): String {
        val target = categoryOf(targetId)
        val dragged = categoryOf(draggedId)
        val category = if (target != null && target == dragged) target else target ?: dragged
        val stamp = now.toString(36)
        return if (category != null) "c_${category.keyPart}_$stamp" else "f$stamp"
    }

    /** The name a folder shows while the user has not given it one of their own. */
    fun folderTitleRes(key: String): Int = when {
        key == "tools" -> R.string.home_folder_tools
        key.startsWith("c_") ->
            Category.entries.firstOrNull { key.startsWith("c_${it.keyPart}_") }?.titleRes ?: R.string.home_folder_new
        else -> R.string.home_folder_new
    }

    fun gridById(id: String): HomeApp? = GRID_DEFAULT.firstOrNull { it.id == id }
}
