package com.huashanlunjian.alice_mad_tea_party.entity;

import com.huashanlunjian.alice_mad_tea_party.network.DollControlPacket;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class DollEntity extends LivingEntity {
    protected final Minecraft minecraft = Minecraft.getInstance();
    public Player player;
    public final Input input = new KeyboardInput(Minecraft.getInstance().options);
    private double xLast;
    /**
     * The last Y position which was transmitted to the server, used to determine when the Y position changes and needs to be re-transmitted
     */
    private double yLast1;
    /**
     * The last Z position which was transmitted to the server, used to determine when the Z position changes and needs to be re-transmitted
     */
    private double zLast;
    /**
     * The last yaw value which was transmitted to the server, used to determine when the yaw changes and needs to be re-transmitted
     */
    private float yRotLast;
    /**
     * The last pitch value which was transmitted to the server, used to determine when the pitch changes and needs to be re-transmitted
     */
    private float xRotLast;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    @Nullable
    private InteractionHand usingItemHand;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    protected int sprintTriggerTime;
    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();
    private double lastHandleMovementTime = Double.MIN_VALUE;
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private ItemStack bodyArmorItem = ItemStack.EMPTY;


    protected DollEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DollEntity(Player player) {
        super(ModEntities.DEMOSONG.get(), player.level());
        this.player=player;

        this.setPos(player.getX(), player.getY(), player.getZ());
    }
    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 90.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MAX_HEALTH, 20.0);
    }
    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }



    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()){
            if (this.isControlledCamera()) {
                this.input.tick(false, 0.0F);
                this.xxa = this.input.leftImpulse;
                this.zza = this.input.forwardImpulse;
                this.jumping = this.input.jumping;
                this.yBobO = this.yBob;
                this.xBobO = this.xBob;
                this.xBob = this.xBob + (this.getXRot() - this.xBob) * 0.5F;
                this.yBob = this.yBob + (this.getYRot() - this.yBob) * 0.5F;
                var event = net.neoforged.neoforge.client.ClientHooks.getTurnPlayerValues(this.minecraft.options.sensitivity().get(), this.minecraft.options.smoothCamera);
                double d12 = event.getMouseSensitivity() * 0.6F + 0.2F;
                double d13 = d12 * d12 * d12;
                double d14 = d13 * 8.0;
                double d10;
                double d11;
                double d00 = Blaze3D.getTime();
                double d01 = d00 - this.lastHandleMovementTime;
                this.lastHandleMovementTime = d00;
                if (event.getCinematicCameraEnabled()) {
                    double d5 = this.smoothTurnX.getNewDeltaValue(this.minecraft.mouseHandler.getXVelocity() * d14, d01 * d14);
                    double d6 = this.smoothTurnY.getNewDeltaValue(this.minecraft.mouseHandler.getYVelocity() * d14, d01 * d14);
                    d10 = d5;
                    d11 = d6;
                } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
                    this.smoothTurnX.reset();
                    this.smoothTurnY.reset();
                    d10 = this.minecraft.mouseHandler.getXVelocity() * d13;
                    d11 = this.minecraft.mouseHandler.getYVelocity() * d13;
                } else {
                    this.smoothTurnX.reset();
                    this.smoothTurnY.reset();
                    d10 = this.minecraft.mouseHandler.getXVelocity() * d14;
                    d11 = this.minecraft.mouseHandler.getYVelocity() * d14;
                }

                int i = 1;

                if (this.minecraft.options.invertYMouse().get()) {
                    i = -1;
                }

                //this.minecraft.getTutorial().onMouse(d10, d11);
                //this.turn(d10, d11 * (double)i);


                double d4 = this.getX() - this.xLast;
                double d0 = this.getY() - this.yLast1;
                double d1 = this.getZ() - this.zLast;
                double d2 = this.getYRot() - this.yRotLast;
                double d3 = this.getXRot() - this.xRotLast;


                boolean flag1 = Mth.lengthSquared(d4, d0, d1) > Mth.square(2.0E-4); //|| this.positionReminder >= 20;
                boolean flag2 = d2 != 0.0 || d3 != 0.0;


                if (flag1) {
                    this.xLast = this.getX();
                    this.yLast1 = this.getY();
                    this.zLast = this.getZ();
                    //this.positionReminder = 0;
                }

                if (flag2) {
                    this.yRotLast = this.getYRot();
                    this.xRotLast = this.getXRot();
                }

                Vec3 vec3 = this.getDeltaMovement();
                PacketDistributor.sendToServer(new DollControlPacket(this.getId(), xxa,  this.jumping?1.0f:0.0f, zza, (float)d10,(float)d11*i));




            }
        }
    }
    @Override
    public void aiStep() {
        if (!this.level().isClientSide()) {
            boolean flag1 = this.input.shiftKeyDown;
            this.minecraft.getTutorial().onInput(this.input);
            if (this.isUsingItem() && !this.isPassenger()) {
                this.input.leftImpulse *= 0.2F;
                this.input.forwardImpulse *= 0.2F;
                this.sprintTriggerTime = 0;
            }

            if (this.autoJumpTime > 0) {
                this.autoJumpTime--;
                this.input.jumping = true;
            }

            if (flag1) {
                this.sprintTriggerTime = 0;
            }
            boolean flag5 = this.isPassenger() ? this.getVehicle().onGround() : this.onGround();
            if (!flag5 && !this.isUnderWater()) {
                this.canStartSwimming();
            }


            if (this.isSprinting()) {
                boolean flag7 = !this.input.hasForwardImpulse();
                boolean flag8 = flag7 || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater() || (this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType)) && !this.canStartSwimming());
                if (this.isSwimming()) {
                    if (!this.onGround() && !this.input.shiftKeyDown && flag7 || !(this.isInWater() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType)))) {
                        this.setSprinting(false);
                    }
                } else if (flag8) {
                    this.setSprinting(false);
                }
            }
            net.neoforged.neoforge.fluids.FluidType fluidType = this.getMaxHeightFluidType();
            if ((this.isInWater() || (!fluidType.isAir() && this.canSwimInFluidType(fluidType))) && this.input.shiftKeyDown && this.isAffectedByFluids()) {
                this.sinkInFluid(this.isInWater() ? net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value() : fluidType);
            }
            if (this.isEyeInFluid(FluidTags.WATER)) {
                int i = this.isSpectator() ? 10 : 1;
                this.waterVisionTime = Mth.clamp(this.waterVisionTime + i, 0, 600);
            } else if (this.waterVisionTime > 0) {
                this.isEyeInFluid(FluidTags.WATER);
                this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
            }
        }


        super.aiStep();

    }



    @Override
    public void move(MoverType type, Vec3 pos) {
        double d0 = this.getX();
        double d1 = this.getZ();
        super.move(type, pos);
        this.updateAutoJump((float)(this.getX() - d0), (float)(this.getZ() - d1));
    }
    protected void updateAutoJump(float movementX, float movementZ) {
        Vec3 vec3 = this.position();
        Vec3 vec31 = vec3.add((double)movementX, 0.0, (double)movementZ);
        Vec3 vec32 = new Vec3((double)movementX, 0.0, (double)movementZ);
        float f = this.getSpeed();
        float f1 = (float)vec32.lengthSqr();
        if (f1 <= 0.001F) {
            Vec2 vec2 = this.input.getMoveVector();
            float f2 = f * vec2.x;
            float f3 = f * vec2.y;
            float f4 = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            float f5 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            vec32 = new Vec3((double)(f2 * f5 - f3 * f4), vec32.y, (double)(f3 * f5 + f2 * f4));
            f1 = (float)vec32.lengthSqr();
            if (f1 <= 0.001F) {
                return;
            }
        }

        float f12 = Mth.invSqrt(f1);
        Vec3 vec312 = vec32.scale((double)f12);
        Vec3 vec313 = this.getForward();
        float f13 = (float)(vec313.x * vec312.x + vec313.z * vec312.z);
        if (!(f13 < -0.15F)) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            BlockPos blockpos = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                blockpos = blockpos.above();
                BlockState blockstate1 = this.level().getBlockState(blockpos);
                if (blockstate1.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                    float f6 = 7.0F;
                    float f7 = 1.2F;
                    if (this.hasEffect(MobEffects.JUMP)) {
                        f7 += (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75F;
                    }

                    float f8 = Math.max(f * 7.0F, 1.0F / f12);
                    Vec3 vec34 = vec31.add(vec312.scale((double)f8));
                    float f9 = this.getBbWidth();
                    float f10 = this.getBbHeight();
                    AABB aabb = new AABB(vec3, vec34.add(0.0, (double)f10, 0.0)).inflate((double)f9, 0.0, (double)f9);
                    Vec3 $$23 = vec3.add(0.0, 0.51F, 0.0);
                    vec34 = vec34.add(0.0, 0.51F, 0.0);
                    Vec3 vec35 = vec312.cross(new Vec3(0.0, 1.0, 0.0));
                    Vec3 vec36 = vec35.scale((double)(f9 * 0.5F));
                    Vec3 vec37 = $$23.subtract(vec36);
                    Vec3 vec38 = vec34.subtract(vec36);
                    Vec3 vec39 = $$23.add(vec36);
                    Vec3 vec310 = vec34.add(vec36);
                    Iterable<VoxelShape> iterable = this.level().getCollisions(this, aabb);
                    Iterator<AABB> iterator = StreamSupport.stream(iterable.spliterator(), false)
                            .flatMap(p_234124_ -> p_234124_.toAabbs().stream())
                            .iterator();
                    float f11 = Float.MIN_VALUE;

                    while (iterator.hasNext()) {
                        AABB aabb1 = iterator.next();
                        if (aabb1.intersects(vec37, vec38) || aabb1.intersects(vec39, vec310)) {
                            f11 = (float)aabb1.maxY;
                            Vec3 vec311 = aabb1.getCenter();
                            BlockPos blockpos1 = BlockPos.containing(vec311);

                            for (int i = 1; (float)i < f7; i++) {
                                BlockPos blockpos2 = blockpos1.above(i);
                                BlockState blockstate2 = this.level().getBlockState(blockpos2);
                                VoxelShape voxelshape;
                                if (!(voxelshape = blockstate2.getCollisionShape(this.level(), blockpos2, collisioncontext)).isEmpty()) {
                                    f11 = (float)voxelshape.max(Direction.Axis.Y) + (float)blockpos2.getY();
                                    if ((double)f11 - this.getY() > (double)f7) {
                                        return;
                                    }
                                }

                                if (i > 1) {
                                    blockpos = blockpos.above();
                                    BlockState blockstate3 = this.level().getBlockState(blockpos);
                                    if (!blockstate3.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                                        return;
                                    }
                                }
                            }
                            break;
                        }
                    }

                    if (f11 != Float.MIN_VALUE) {
                        float f14 = (float)((double)f11 - this.getY());
                        if (!(f14 <= 0.5F) && !(f14 > f7)) {
                            this.autoJumpTime = 1;
                        }
                    }
                }
            }
        }
    }
    @Override
    public boolean isShiftKeyDown() {
        return this.input != null && this.input.shiftKeyDown;
    }
    public static DollEntity getDoll(Player player,int id){
        return (DollEntity)player.level().getEntity(id);
//        Level level = player.level();
//        //return level.getEntities(DollEntity.class,new AABB(player.blockPosition()).inflate(50))
//        for (DollEntity dollEntity : level.getEntitiesOfClass(DollEntity.class,new AABB(player.blockPosition()).inflate(50))) {
//            if (dollEntity.getUUID().equals(uuid)){
//                return dollEntity;
//            }
//        }
//        return getDoll(player,uuid);
    }
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> (ItemStack)this.handItems.get(slot.getIndex());
            case HUMANOID_ARMOR -> (ItemStack)this.armorItems.get(slot.getIndex());
            case ANIMAL_ARMOR -> this.bodyArmorItem;
        };
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch (slot.getType()) {
            case HAND:
                this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
                break;
            case ANIMAL_ARMOR:
                ItemStack itemstack = this.bodyArmorItem;
                this.bodyArmorItem = stack;
                this.onEquipItem(slot, itemstack, stack);
        }
    }
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;

    }
}
