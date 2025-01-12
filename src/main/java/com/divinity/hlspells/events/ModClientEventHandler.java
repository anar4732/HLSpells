package com.divinity.hlspells.events;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.capabilities.totemcap.ITotemCap;
import com.divinity.hlspells.capabilities.totemcap.TotemItemProvider;
import com.divinity.hlspells.client.other.AltarItemRenderer;
import com.divinity.hlspells.client.renderers.BaseBoltRenderer;
import com.divinity.hlspells.client.renderers.FireballRenderer;
import com.divinity.hlspells.compat.CuriosCompat;
import com.divinity.hlspells.entities.projectile.InvisibleTargetingEntity;
import com.divinity.hlspells.particle.custom.BoltBoomParticle;
import com.divinity.hlspells.particle.custom.RuneParticle;
import com.divinity.hlspells.setup.init.*;
import com.divinity.hlspells.world.blocks.blockentities.screen.AltarOfAttunementScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;



@Mod.EventBusSubscriber(modid = HLSpells.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ModClientEventHandler {

        @SubscribeEvent
        public static void init(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                registerItemModel(ItemInit.SPELL_BOOK.get(), new ResourceLocation("using"), 3, 0.2F, 0.4F, 0.6F, 0.8F, 1F);
                registerItemModel(ItemInit.WAND.get(), new ResourceLocation("pull"), 3, 0.2F, 0.4F, 0.6F, 0.8F, 1F);
                registerItemModel(ItemInit.AMETHYST_WAND.get(), new ResourceLocation("pull"), 3, 0.2F, 0.4F, 0.6F, 0.8F, 1F);
                ItemInit.STAFFS.forEach(staff -> registerItemModel(staff.get(), new ResourceLocation("pull"), 3, 0.2F, 0.4F, 0.6F, 0.8F, 1F));
                ItemProperties.register(ItemInit.TOTEM_OF_RETURNING.get(), new ResourceLocation("used"), (stack, world, living, integer) -> {
                    if (living instanceof Player) {
                        var totemCap = stack.getCapability(TotemItemProvider.TOTEM_CAP);
                        if (totemCap.isPresent())
                            return totemCap.map(ITotemCap::getHasDied).orElse(false) ? 1 : 0;
                    }
                    return 0;
                });
            });
            // Curios Renderer Registration
            if (HLSpells.isCurioLoaded) {
                CuriosCompat.renderCuriosTotems(ItemInit.TOTEMS);
            }
            MenuScreens.register(MenuTypeInit.ALTAR_CONTAINER.get(), AltarOfAttunementScreen::new);
            ItemBlockRenderTypes.setRenderLayer(BlockInit.ALTAR_OF_ATTUNEMENT_BLOCK.get(), RenderType.cutout());
            BlockEntityRenderers.register(BlockInit.ALTAR_BE.get(), ctx -> new AltarItemRenderer());
        }
//        @SubscribeEvent
//        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
//            event.registerLayerDefinition(BaseBoltModel.LAYER_LOCATION, BaseBoltModel::createBodyLayer);
//
//            event.registerLayerDefinition(FireballModel.LAYER_LOCATION, FireballModel::createBodyLayer);

        @SuppressWarnings("all")
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityInit.INVISIBLE_TARGETING_ENTITY.get(), ctx -> new EntityRenderer<>(ctx) {
                @Override
                public boolean shouldRender(InvisibleTargetingEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
                    return false;
                }
                @Override
                public ResourceLocation getTextureLocation(InvisibleTargetingEntity pEntity) {
                    return new ResourceLocation("");
                }
            });
            event.registerEntityRenderer(EntityInit.PIERCING_BOLT_ENTITY.get(), ctx -> new BaseBoltRenderer<>(ctx, getBoltLocation("textures/entity/bolt/green_bolt.png")));
            event.registerEntityRenderer(EntityInit.FLAMING_BOLT_ENTITY.get(), ctx -> new BaseBoltRenderer<>(ctx, getBoltLocation("textures/entity/bolt/orange_bolt.png")));
            event.registerEntityRenderer(EntityInit.AQUA_BOLT_ENTITY.get(), ctx -> new BaseBoltRenderer<>(ctx, getBoltLocation("textures/entity/bolt/blue_bolt.png")));
            event.registerEntityRenderer(EntityInit.FREEZING_BOLT_ENTITY.get(), ctx -> new BaseBoltRenderer<>(ctx, getBoltLocation("textures/entity/bolt/white_bolt.png")));
            event.registerEntityRenderer(EntityInit.CHORUS_BOLT_ENTITY.get(), ctx -> new BaseBoltRenderer<>(ctx, getBoltLocation("textures/entity/bolt/purple_bolt.png")));
            event.registerEntityRenderer(EntityInit.FIREBALL.get(), ctx -> new FireballRenderer<>(ctx, getBoltLocation("textures/entity/fireball/fireball1.png")));
            event.registerEntityRenderer(EntityInit.FIREBALL2.get(), ctx -> new FireballRenderer<>(ctx, getBoltLocation("textures/entity/fireball/fireball2.png")));
            event.registerEntityRenderer(EntityInit.KNOCKBACK_BOLT_ENTITY.get(), ShulkerBulletRenderer::new);
            event.registerEntityRenderer(EntityInit.SUMMONED_VEX_ENTITY.get(), VexRenderer::new);
            event.registerEntityRenderer(EntityInit.SUMMONED_SKELETON_ENTITY.get(), SkeletonRenderer::new);
            event.registerEntityRenderer(EntityInit.WITHER_SKULL_ENTITY.get(), WitherSkullRenderer::new);
        }

        /**
         * Registers the model of a given item that pulls back like a bow
         *
         * @param item                    The item to register the model of
         * @param location                The resource location of the model predicate
         * @param useItemRemainTickOffset The tick offset at which the item will change models at
         * @param values                  The amount of model increments at which it changes at
         */
        @SuppressWarnings("all")
        private static void registerItemModel(Item item, ResourceLocation location, int useItemRemainTickOffset, float... values) {
            ItemProperties.register(item, location, (stack, world, living, seed) -> {
                if (living instanceof Player && living.isUsingItem() && living.getUseItem() == stack) {
                    int useDuration = item.getUseDuration(item.getDefaultInstance());
                    int minUseAmount = useDuration - (useItemRemainTickOffset * (values.length - 1));
                    for (int i = 0; i < values.length; i++) {
                        if ((double) living.getUseItemRemainingTicks() < minUseAmount) return values[values.length - 1];
                        else if ((double) living.getUseItemRemainingTicks() < useDuration && (double) living.getUseItemRemainingTicks() >= (useDuration - (useItemRemainTickOffset * (i == 0 ? 1 : i)))) {
                            return values[i];
                        }
                    }
                }
                return 0;
            });
        }

        private static ResourceLocation getBoltLocation(String location) {
            return new ResourceLocation(HLSpells.MODID, location);
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            Minecraft.getInstance().particleEngine.register(ParticlesInit.GREEN_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLACK_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.RED_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.WHITE_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.ORANGE_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.PURPLE_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLUE_PARTICLE.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.YELLOW_PARTICLE.get(), RuneParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(ParticlesInit.GREEN_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLACK_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.RED_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.WHITE_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.ORANGE_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.PURPLE_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLUE_PARTICLE_SMALL.get(), RuneParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.YELLOW_PARTICLE_SMALL.get(), RuneParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(ParticlesInit.ORANGE_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLUE_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.BLACK_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.WHITE_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.PURPLE_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticlesInit.GREEN_BOLT_BOOM.get(), BoltBoomParticle.Provider::new);

        }

}



