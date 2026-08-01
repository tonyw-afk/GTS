package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Items;
import org.pokesplash.gts.UI.HistoryBought;
import org.pokesplash.gts.enumeration.Sort;

public class HistoryBoughtButton {
    public static Button getButton() {
        return GooeyButton.builder()
                .display(Items.EMERALD.getDefaultInstance())
                .with(DataComponents.CUSTOM_NAME, Component.literal("§aPurchase History"))
                .with(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();
                    Page page = new HistoryBought().getPage(action.getPlayer().getUUID(), Sort.NONE);
                    UIManager.openUIForcefully(sender, page);
                })
                .build();
    }
}
