# Launchpad

Launchpad is a simple but configurable plugin able to make any placeable block into a launchpad which launches a player
with the desired velocity in their current or in a fixed direction. While anything can be a launchpad, pressure plates
have more accurate hit-detection as that's built into Minecraft.

Launchpads will prioritize values specifically set for that launchpad. If not set, they will look for material-specific
values. If not set, they will look for the generic velocities in the config.

## Known problems

#### Jumping on a pressure plate causes a `Player moved too quickly` message in the console, and the player is weirdly glitched for a moment

This happens because the server thinks the player moved faster than it should, and forcefully prevents the player from
moving. Increasing `moved-too-quickly-multiplier` in `spigot.yml` may fix this issue.

## Commands

Note that changing a property for a block which isn't currently a launchpad will turn the block into a launchpad.
If you alter several launchpad values in succession, they'll all be applied to the next block you right-click.

| Command                       | Arguments                                           | Description                                                                                              |
|-------------------------------|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| /launchpad add                |                                                     | Makes the clicked block into a launchpad.                                                                |
| /launchpad remove             |                                                     | Removes the clicked block as a launchpad.                                                                |
| /launchpad abort              |                                                     | Clears any unprocessed launchpad modifications.                                                          |
| /launchpad verticalVelocity   | Decimal number / "null"                             | Sets the vertical velocity for the clicked launchpad. Use "null" to unset.                               |
| /launchpad horizontalVelocity | Decimal number / "null"                             | Sets the horizontal velocity for the clicked launchpad. Use "null" to unset.                             |
| /launchpad velocities         | <Decimal Number / "null"> <Decimal Number / "null"> | Sets the horizontal and vertical velocities at once.  The first argument is for the horizontal velocity. |
| /launchpad fixedDirection     | NORTH / SOUTH / EAST / WEST / "null"                | Sets a fixed direction the launchpad will launch every player. Use "null" to unset.                      |
| /launchpad:reload             |                                                     | Reloads the configuration and launchpads from disk.                                                      |

## Permissions

| Permission       | Description                                       |
|------------------|---------------------------------------------------|
| launchpad.admin  | Gives all other permissions                       |
| launchpad.modify | Allows modifying, adding and removing launchpads. |
| launchpad.reload | Allows reloading the launchpad plugin.            |

## Configuration

| Node                                                        | Type                                                                          | Description                                                                                                                                                                                                                      |
|-------------------------------------------------------------|-------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| launchpad.materials                                         | List                                                                          | A list of materials, or material tags (+TAG_NAME), which are always treated as launchpads, without the need for manual registration.                                                                                             |
| launchpad.materialWhitelist                                 | List                                                                          | A list of materials, or material tags (+TAG_NAME), which can be manually turned into launchpads. Use this to prevent unwanted blocks from being turned into launchpads.                                                          |
| launchpad.verticalVelocity                                  | Decimal number                                                                | The vertical (upwards) velocity applied to launchpads if not specified otherwise.                                                                                                                                                |
| launchpad.horizontalVelocity                                | Decimal number                                                                | The horizontal (sideways) velocity applied to launchpads if not specified otherwise.                                                                                                                                             |
| launchpad.materialVelocities.\<MATERIAL>.horizontalVelocity | Decimal number                                                                | The horizontal (sideways) velocity applied to launchpads of type \<MATERIAL> if not overridden for the block.                                                                                                                    |
| launchpad.materialVelocities.\<MATERIAL>.verticalVelocity   | Decimal number                                                                | The vertical (sideways) velocity applied to launchpads of type \<MATERIAL> if not overridden for the block.                                                                                                                      |
| launchpad.particles.trailsEnabled                           | True / False                                                                  | Whether to enable particle trails behind players                                                                                                                                                                                 |
| launchpad.particles.trailSpawnDelay                         | Positive integer                                                              | The amount of ticks (1 second = 20 ticks) between each time the particle(s) of a trail should be spawned.                                                                                                                        |
| launchpad.particles.trailType                               | [Particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html) | The type of trail to spawn behind launched players.                                                                                                                                                                              |
| launchpad.particles.randomTrailType                         | True / False                                                                  | Whether to use a random value from randomTrailWhitelist as the trail on each launch.                                                                                                                                             |
| launchpad.particles.randomTrailWhitelist                    | List                                                                          | A list of all [particles](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html) selectable for random trails.                                                                                                       |
| launchpad.particles.enabled                                 | True / False                                                                  | Whether to display some kind of particle effect above manually added launchpads.                                                                                                                                                 |
| launchpad.particles.spawnDelay                              | Positive integer                                                              | The amount of ticks (1 second = 20 ticks) between each time the particle(s) should be spawned again. Depending on the particle, higher values will make the particle(s) completely disappear and reappear.                       |
| launchpad.particles.particle.mode                           | SINGLE / SQUARE / PYRAMID / SPHERE / CIRCLE / CUBE                            | The mode used for drawing particles. SINGLE directly spawns the particle(s) in one spot above the launchpad. The other ones spawn particles a bunch of times in a pattern.                                                       |
| launchpad.particles.particle.type                           | [Particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html) | The type of particle to spawn above launchpads.                                                                                                                                                                                  |
| launchpad.particles.particle.amount                         | Positive integer                                                              | The amount of particles to spawn. Use 1 if mode is anything except SINGLE, unless you know what you are doing!                                                                                                                   |
| launchpad.particles.particle.offsetX                        | Decimal number                                                                | The offset, or spread of the particles in the X direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.particle.offsetY                        | Decimal number                                                                | The offset, or spread of the particles in the Y direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.particle.offsetZ                        | Decimal number                                                                | The offset, or spread of the particles in the Z direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.particle.heightOffset                   | Decimal number                                                                | The amount of blocks above the launchpad the particle should spawn. 0.5 = half a block. 1 = one block.                                                                                                                           |
| launchpad.particles.particle.particleDensity                | Decimal number                                                                | A definition for the number of particles used to draw shapes. The number of particles is basically `distance / particleDensity`, so lower numbers create a more dense shape.                                                     |
| launchpad.particles.particle.extra                          | Decimal number                                                                | Extra data for the specific particle. Check the Spigot documentation for details.                                                                                                                                                |
| launchpad.particles.materialParticles                       | Configuration section                                                         | This section allows specifying different particle configurations for a material or a material tag. So you'd set materialParticles.LIGHT_WEIGHTED_PRESSURE_PLATE.type to set the particle type for LIGHT_WEIGHTED_PRESSURE_PLATE. |

## Language customization

Strings shown to users are customizable. If you place a strings.yml file in the plugin folder, it will take
priority over built-in languages. If you want to change strings, look at Launchpad/src/main/resources/strings.yml for
the proper keys. All strings have the format: ENUM: "Displayed string". The enum must be identical as it defines which
string you have changed. All strings belonging to a language are beneath the language code and indented with two spaces.

The easiest way to add a new language is to copy an existing language and paste it into your custom strings.yml and
change strings as necessary. If you don't include all strings, the remaining will use the built-in English translation.
Remember to change the language code to whichever you use for your custom language.

The interval messages are unique in that if several values are separated by comma (option1,option2,option3), a random
message will be chosen each time it's displayed.

## License

Launchpad is licensed under the GNU Public License Version 3.0. This includes every source and resource file. See the
HEADER file for a more detailed license description.