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
import org.pokesplash.gts.UI.History;
import org.pokesplash.gts.enumeration.Sort;

public class HistoryButton {
    public static Button getButton(Sort sort) {
        return GooeyButton.builder()
                .display(Items.ENDER_CHEST.getDefaultInstance())
                .with(DataComponents.CUSTOM_NAME, Component.literal("§8History"))
                .with(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();
                    Page page = new History().getPage(action.getPlayer().getUUID(), sort);
                    UIManager.openUIForcefully(sender, page);
                })
                .build();
    }
}
