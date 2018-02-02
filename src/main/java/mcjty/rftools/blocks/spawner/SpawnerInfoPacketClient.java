package mcjty.rftools.blocks.spawner;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.clientinfo.InfoPacketClient;
import net.minecraft.client.entity.EntityPlayerSP;

public class SpawnerInfoPacketClient implements InfoPacketClient {

    private long[] matter;

    public static long matterReceived[] = null;

    public SpawnerInfoPacketClient() {
    }

    public SpawnerInfoPacketClient(long[] matter) {
        this.matter = matter;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            matter = new long[] {
                buf.readLong(), buf.readLong(), buf.readLong()
            };
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (matter == null || matter.length < 3) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeLong(matter[0]);
            buf.writeLong(matter[1]);
            buf.writeLong(matter[2]);
        }
    }

    @Override
    public void onMessageClient(EntityPlayerSP player) {
        matterReceived = matter;
    }
}
