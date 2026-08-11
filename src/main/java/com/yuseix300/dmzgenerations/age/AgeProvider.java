package com.yuseix300.dmzgenerations.age;

import com.yuseix300.dmzgenerations.DMZGenerations;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AgeProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final ResourceLocation ID = new ResourceLocation(DMZGenerations.MOD_ID, "age");

    private final AgeData data = new AgeData();
    private final LazyOptional<AgeData> optional = LazyOptional.of(() -> data);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == AgeCapability.INSTANCE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.save();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.load(nbt);
    }
}
