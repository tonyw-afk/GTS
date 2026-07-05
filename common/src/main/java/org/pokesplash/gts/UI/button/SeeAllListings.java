package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.AllListings;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

public class SeeAllListings {
    public static Button getButton(Sort sort) {
        return GooeyButton.builder()
                .display(Gts.language.getAllListingsButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse(Gts.language.getAllListingsButtonLabel()))
                .with(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();
                    Page page = new AllListings().getPage(sort);
                    UIManager.openUIForcefully(sender, page);
                })
                .build();
    }
}
