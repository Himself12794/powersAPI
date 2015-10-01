# Powers API
Facilitating Adding Powers/Spells to Minecraft

This API just adds an interface to make it easier to create any kind of power to Minecraft.

All you need to do is extend one of the power classes, and then register via Power.register(Power power).
All registered powers will be useable by the power actuator item, assuming that its appearence is allowed via config.

Power effects can be registered in a similar manner.
