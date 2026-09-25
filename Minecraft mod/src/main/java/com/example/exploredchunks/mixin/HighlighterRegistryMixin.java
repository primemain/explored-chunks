package com.example.exploredchunks.mixin;

import com.example.exploredchunks.ExploredHighlighter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.highlight.HighlighterRegistry;

/** Xaero's fills this registry when a minimap session starts, then locks it in end(). Add ours just before. */
@Mixin(value = HighlighterRegistry.class, remap = false)
public abstract class HighlighterRegistryMixin {
    @Inject(method = "end", at = @At("HEAD"))
    private void exploredchunks$register(CallbackInfo ci) {
        ((HighlighterRegistry) (Object) this).register(new ExploredHighlighter());
    }
}
