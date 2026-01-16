package net.spudacious5705.SpudsTest;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;


/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class ExampleCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Integer> radiusArg;

    public ExampleCommand(String pluginName, String pluginVersion) {
        super("pre-gen", "Generates a square of chunks from a radii");
        this.setPermissionGroup(GameMode.Creative);
        this.radiusArg = this.withRequiredArg("radius", "server.commands.player.viewradius.set.radius.desc", ArgTypes.INTEGER);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        int i = this.radiusArg.get(commandContext);
        new ChunkGenerator(i, 16, world, commandContext).start();
    }
}