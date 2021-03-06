package mcjty.rftools.craftinggrid;

import io.netty.buffer.ByteBuf;
import mcjty.rftools.RFTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGridToClient extends PacketGridSync implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
        convertFromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        convertToBytes(buf);
    }

    public PacketGridToClient() {
    }

    public PacketGridToClient(BlockPos pos, CraftingGrid grid) {
        init(pos, grid);
    }

    public static class Handler implements IMessageHandler<PacketGridToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketGridToClient message, MessageContext ctx) {
            World world = RFTools.proxy.getClientWorld();
            EntityPlayer player = RFTools.proxy.getClientPlayer();
            RFTools.proxy.addScheduledTaskClient(() -> message.handleMessage(world, player));
            return null;
        }
    }
}
