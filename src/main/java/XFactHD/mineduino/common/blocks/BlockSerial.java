/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.mineduino.common.blocks;

import XFactHD.mineduino.MineDuino;
import XFactHD.mineduino.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockSerial extends Block
{
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public BlockSerial()
    {
        super(Material.IRON);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "block_serial"));
        setUnlocalizedName(getRegistryName().toString());
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlockSerial(this).setRegistryName(getRegistryName()));
        GameRegistry.registerTileEntity(TileEntitySerialSender.class, "SerialSender");
        GameRegistry.registerTileEntity(TileEntitySerialReceiver.class, "SerialReceiver");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(TYPE, Type.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            player.openGui(MineDuino.INSTANCE, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return state.getValue(TYPE) == Type.SENDER ? new TileEntitySerialSender() : new TileEntitySerialReceiver();
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return state.getValue(TYPE) == Type.RECEIVER;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (state.getValue(TYPE) == Type.RECEIVER)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntitySerialReceiver && side.getAxis().isHorizontal())
            {
                return ((TileEntitySerialReceiver)te).getPower();
            }
        }
        return 0;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess word, BlockPos pos, EnumFacing side)
    {
        return getStrongPower(state, word, pos, side);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) { te.invalidate(); }
        super.breakBlock(world, pos, state);
        world.notifyNeighborsOfStateChange(pos, this);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getBlock().getMetaFromState(state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        AxisAlignedBB aabb = getCollisionBoundingBox(state, world, pos);
        return rayTrace(pos, start, end, aabb != null ? aabb : Block.FULL_BLOCK_AABB);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(0, 0, 0, 1, 6F/16F, 1);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        //noinspection ConstantConditions
        return getCollisionBoundingBox(state, world, pos).offset(pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    public enum Type implements IStringSerializable
    {
        SENDER,
        RECEIVER;

        @Override
        public String getName()
        {
            return this == SENDER ? "tx" : "rx";
        }
    }

    public static class ItemBlockSerial extends ItemBlock
    {
        public ItemBlockSerial(Block b)
        {
            super(b);
            setHasSubtypes(true);
        }

        @Override
        public int getMetadata(int damage)
        {
            return damage;
        }

        @Override
        public String getUnlocalizedName(ItemStack stack)
        {
            return getUnlocalizedName() + (stack.getMetadata() == 0 ? "_tx" : "_rx");
        }
    }
}