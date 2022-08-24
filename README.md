![Power Scale](.github/images/title.png)

<div align="center">

<a href="">![Java 17](https://img.shields.io/badge/Java%2017-ee9258?logo=coffeescript&logoColor=ffffff&labelColor=606060&style=flat-square)</a>
<a href="">![Environment: Server](https://img.shields.io/badge/environment-Server-1976d2?style=flat-square)</a>
<a href="">[![Discord](https://img.shields.io/discord/973561601519149057.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2&style=flat-square)](https://discord.gg/KN9b3pjFTM)</a>
</br>
<h3>🔧 = this mod | 🔩 = entity & loot attributes
</div>

# 📦 Features

- Dimension and biome specific ruleset
- Each ruleset can adjust entities and looted items
- Entities attributes and spawner settings can be adjusted (for example: more health, armor, etc...)
- Looted item attributes can be adjusted (for example: more damage on weapons)
- Fully data-driven (using the configuration file)

# 🔧 Configuration

The configuration is meant to be used by modpack developers, hence no in-game (client-side) settings are available.

Server side configuration can be found at `config/powerscale.json`.

Use the following command to refresh the config while in game: `/powerscale_config_reload`

Config file is parsed into `Config` object. You can find it [here](./src/main/java/net/powerscale/config/Config.java).

Config file is **sanitized** upon reloading, meaning every non parsable data is removed.

Regex fields in the configuration are interpreted as fully featured regex. Suggested regex testing tool: [regex101.com](https://regex101.com) 