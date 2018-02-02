package mcjty.rftools.blocks.spawner;

import mcjty.lib.api.IModuleSupport;
import mcjty.lib.api.Infusable;
import mcjty.lib.network.clientinfo.PacketGetInfoFromServer;
import mcjty.lib.varia.ModuleSupport;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import mcjty.rftools.items.ModItems;
import mcjty.rftools.network.RFToolsMessages;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class SpawnerBlock extends GenericRFToolsBlock<SpawnerTileEntity, SpawnerContainer> implements Infusable {

    public SpawnerBlock() {
        super(Material.IRON, SpawnerTileEntity.class, SpawnerContainer.class, "spawner", true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<GuiSpawner> getGuiClass() {
        return GuiSpawner.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This block can spawn creatures. It needs a syringe");
            list.add(TextFormatting.WHITE + "of the appropriate type, RF power and also it");
            list.add(TextFormatting.WHITE + "needs beams of energized matter.");
            list.add(TextFormatting.YELLOW + "Infusing bonus: reduced power usage.");
        } else {
            list.add(TextFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @Override
    protected IModuleSupport getModuleSupport() {
        return new ModuleSupport(SpawnerContainer.SLOT_SYRINGE) {
            @Override
            public boolean isModule(ItemStack itemStack) {
                return itemStack.getItem() == ModItems.syringeItem;
            }
        };
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof SpawnerTileEntity) {
            SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) te;
            long[] matter = spawnerTileEntity.getMatter();
            DecimalFormat fmt = new DecimalFormat("#.##");
            fmt.setRoundingMode(RoundingMode.DOWN);
            probeInfo.text(TextFormatting.GREEN + "Key Matter: " + fmt.format(matter[0]/2520D));
            probeInfo.text(TextFormatting.GREEN + "Bulk Matter: " + fmt.format(matter[1]/2520D));
            probeInfo.text(TextFormatting.GREEN + "Living Matter: " + fmt.format(matter[2]/2520D));
        }
    }

    private static long lastTime = 0;

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof SpawnerTileEntity) {
            SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) te;

            if (System.currentTimeMillis() - lastTime > 500) {
                lastTime = System.currentTimeMillis();
                RFToolsMessages.INSTANCE.sendToServer(new PacketGetInfoFromServer(RFTools.MODID, new SpawnerInfoPacketServer(te.getWorld().provider.getDimension(),
                        te.getPos())));
            }

            long[] matter = SpawnerInfoPacketClient.matterReceived;
            if (matter != null && matter.length == 3) {
                DecimalFormat fmt = new DecimalFormat("#.##");
                fmt.setRoundingMode(RoundingMode.DOWN);
                currenttip.add(TextFormatting.GREEN + "Key Matter: " + fmt.format(matter[0]/2520D));
                currenttip.add(TextFormatting.GREEN + "Bulk Matter: " + fmt.format(matter[1]/2520D));
                currenttip.add(TextFormatting.GREEN + "Living Matter: " + fmt.format(matter[2]/2520D));
            }
        }
        return currenttip;
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_SPAWNER;
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (world.isRemote) {
            SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) world.getTileEntity(pos);
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvent.REGISTRY.getObject(new ResourceLocation("block.note.pling")), SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            spawnerTileEntity.useWrench(player);
        }
        return true;
    }
}
