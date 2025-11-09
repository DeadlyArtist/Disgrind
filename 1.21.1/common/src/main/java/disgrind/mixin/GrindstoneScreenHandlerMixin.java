package disgrind.mixin;

import disgrind.utils.GrindstoneUtils;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    @Unique
    private final GrindstoneScreenHandler self = (GrindstoneScreenHandler) (Object) this;

    @Final
    @Shadow
    private Inventory result;

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResults(CallbackInfo ci) {
        var slot1 = self.getSlot(0).getStack();
        var slot2 = self.getSlot(1).getStack();
        if (!GrindstoneUtils.isSpecialTransfer(slot1, slot2)) return;
        var output = GrindstoneUtils.getOutput(slot1, slot2);
        result.setStack(0, output);

        ci.cancel();
    }
}
