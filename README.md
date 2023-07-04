# Launchpad

Launchpad is a simple but configurable plugin able to make any placeable block into a launchpad which launches a player
with the desired velocity in their current or in a fixed direction. While anything can be a launchpad, pressure plates
have more accurate hit-detection as that's built into Minecraft.

Launchpads will prioritize values specifically set for that launchpad. If not set, they will look for material-specific
values. If not set, they will look for the generic velocities in the config.

## Commands

Note that changing a property for a block which isn't currently a launchpad will turn the block into a launchpad.
If you alter several launchpad values in succession, they'll all be applied to the next block you right-click.

| Command                       | Arguments                            | Description                                                                         |
|-------------------------------|--------------------------------------|-------------------------------------------------------------------------------------|
| /launchpad add                |                                      | Makes the clicked block into a launchpad.                                           |
| /launchpad remove             |                                      | Removes the clicked block as a launchpad.                                           |
| /launchpad abort              |                                      | Clears any unprocessed launchpad modifications.                                     |
| /launchpad verticalVelocity   | Decimal number / "null"              | Sets the vertical velocity for the clicked launchpad. Use "null" to unset.          |
| /launchpad horizontalVelocity | Decimal number / "null"              | Sets the horizontal velocity for the clicked launchpad. Use "null" to unset.        |
| /launchpad fixedDirection     | NORTH / SOUTH / EAST / WEST / "null" | Sets a fixed direction the launchpad will launch every player. Use "null" to unset. |
| /launchpad:reload             |                                      | Reloads the configuration and launchpads from disk.                                 |

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
| launchpad.particles.enabled                                 | True / False                                                                  | Whether to display some kind of particle effect above manually added launchpads.                                                                                                                                                 |
| launchpad.particles.mode                                    | SINGLE / SQUARE / PYRAMID / SPHERE / CIRCLE / CUBE                            | The mode used for drawing particles. SINGLE directly spawns the particle(s) in one spot above the launchpad. The other ones spawn particles a bunch of times in a pattern.                                                       |
| launchpad.particles.type                                    | [Particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html) | The type of particle to spawn above launchpads.                                                                                                                                                                                  |
| launchpad.particles.amount                                  | Positive integer                                                              | The amount of particles to spawn. Use 1 if mode is anything except SINGLE, unless you know what you are doing!                                                                                                                   |
| launchpad.particles.offsetX                                 | Decimal number                                                                | The offset, or spread of the particles in the X direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.offsetY                                 | Decimal number                                                                | The offset, or spread of the particles in the Y direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.offsetZ                                 | Decimal number                                                                | The offset, or spread of the particles in the Z direction, relative to the launchpad                                                                                                                                             |
| launchpad.particles.heightOffset                            | Decimal number                                                                | The amount of blocks above the launchpad the particle should spawn. 0.5 = half a block. 1 = one block.                                                                                                                           |
| launchpad.particles.particleDensity                         | Decimal number                                                                | A definition for the number of particles used to draw shapes. The number of particles is basically `distance / particleDensity`, so lower numbers create a more dense shape.                                                     |
| launchpad.particles.extra                                   | Decimal number                                                                | Extra data for the specific particle. Check the Spigot documentation for details.                                                                                                                                                |
| launchpad.particles.spawnDelay                              | Positive integer                                                              | The amount of ticks (1 second = 20 ticks) between each time the particle(s) should be spawned again. Depending on the particle, higher values will make the particle(s) completely disappear and reappear.                       |
| launchpad.particles.materialParticles                       | Configuration section                                                         | This section allows specifying different particle configurations for a material or a material tag. So you'd set materialParticles.LIGHT_WEIGHTED_PRESSURE_PLATE.type to set the particle type for LIGHT_WEIGHTED_PRESSURE_PLATE. |