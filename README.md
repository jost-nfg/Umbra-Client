<picture>
  <img src="src/main/resources/assets/umbra/textures/mainmenu/umbra-name.png" alt="Umbra" width="360">
</picture>

# Umbra Client (26.2)

**Umbra Client** is a custom utility mod for **Lunar Client 26.2** (Minecraft 26.2). It pairs an intuitive design with powerful features: a fully custom flat menu, a windowed ClickGUI, HUDs, combat/legit PvP modules, and an addon system.

---

### Installation (Lunar Client 26.2)

Umbra runs as a Fabric mod inside Lunar Client's built-in Fabric support — no separate Fabric installation is needed:

1. Open the **Lunar Client Launcher** and select the **26.2** version.
2. Press the **⚙ Settings** button at the bottom right, then open the **Mods** tab.
3. Drag and drop `Umbra-<version>.jar` into the window (or copy it into the profile's mods folder, e.g. `~/.lunarclient/profiles/26/mods/fabric-26.2/`).
4. Launch the game.

Notes when running under Lunar Client:

- The **custom title screen** defaults to off (Lunar has its own main menu). It can be re-enabled in the ClickGUI under UI Options.
- The **DiscordRPC** module is disabled under Lunar, since Lunar provides its own rich presence.
- Sodium is provided by Lunar; Umbra's Sodium-specific XRay integration activates automatically when the bundled Sodium version matches.

---

### Default Keybinds

- `INSERT` – Open/close the **flat menu** (module cards, search, categories).
- `DELETE` – Open/close the **ClickGUI** (movable, pinnable windows).
- `↑` / `↓` – Move the menu cursor.
- `→` – Enter the selected module menu.
- `←` – Exit the current module menu.

---

### Features

- **Flat menu + ClickGUI**: two fully custom-rendered interfaces with themes, shaders, and custom fonts.
- **Legit PvP suite**: AutoClicker (humanized CPS), Aimbot with hold-to-aim, Velocity-style AntiKnockback, capped Reach, TriggerBot with randomized delays.
- **Extensive module library**: 80+ modules across Combat, Movement, Render, World, and Misc with rebindable keybinds.
- **Command system**: in-game commands via the `.umbra` prefix.
- **Alt Manager**: manage and switch between multiple Minecraft accounts.
- **Proxy Manager**: route connections through SOCKS5 proxies.
- **Addon System**: extend the client with custom addons.

---

### Addons

Addons are ordinary Fabric mods dropped into the same mods folder. They must declare their entrypoint under the key `"umbra"` in their `fabric.mod.json` (`"entrypoints": { "umbra": [ ... ] }`) and implement `net.umbra.api.IAddon` to be picked up by the client.

---

### License

This project is licensed under the **GNU General Public License v3 (GPLv3)**. You are free to use, modify, and redistribute the code as long as your adaptations comply with the GPLv3 guidelines.
