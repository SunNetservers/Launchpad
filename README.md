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

| Node                                                       | Type           | Description                                                                                                                                                             |
|------------------------------------------------------------|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| launchpad.materials                                        | List           | A list of materials, or material tags (+TAG_NAME), which are always treated as launchpads, without the need for manual registration.                                    |
| launchpad.materialWhitelist                                | List           | A list of materials, or material tags (+TAG_NAME), which can be manually turned into launchpads. Use this to prevent unwanted blocks from being turned into launchpads. |
| launchpad.verticalVelocity                                 | Decimal number | The vertical (upwards) velocity applied to launchpads if not specified otherwise.                                                                                       |
| launchpad.horizontalVelocity                               | Decimal number | The horizontal (sideways) velocity applied to launchpads if not specified otherwise.                                                                                    |
| launchpad.materialVelocities.<MATERIAL>.horizontalVelocity | Decimal number | The horizontal (sideways) velocity applied to launchpads of type <MATERIAL> if not overridden for the block.                                                            |
| launchpad.materialVelocities.<MATERIAL>.verticalVelocity   | Decimal number | The vertical (sideways) velocity applied to launchpads of type <MATERIAL> if not overridden for the block.                                                              |