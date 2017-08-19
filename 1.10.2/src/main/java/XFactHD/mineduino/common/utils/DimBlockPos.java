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

package XFactHD.mineduino.common.utils;

import net.minecraft.util.math.BlockPos;

public class DimBlockPos extends BlockPos
{
    private BlockPos pos;
    private int dim;

    public DimBlockPos(int dim, int x, int y, int z)
    {
        super(x, y, z);
        this.pos = new BlockPos(x, y, z);
        this.dim = dim;
    }

    public DimBlockPos(int dim, BlockPos pos)
    {
        super(pos);
        this.pos = pos;
        this.dim = dim;
    }

    public int getDim()
    {
        return dim;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    @Override
    public boolean equals(Object other)
    {
        boolean instance = other instanceof DimBlockPos;
        return super.equals(other) && instance && dim == ((DimBlockPos)other).dim;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() + dim;
    }
}