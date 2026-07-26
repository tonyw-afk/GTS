package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ItemLore;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for confirming listing
 */

public class ConfirmListing {
    public Page getPage(ServerPlayer player, Listing listing, Integer slot, double minPrice, String speciesName, double price) {
        List<Component> lore = new ArrayList<>();

        if (listing.isPokemon()) {
            lore.addAll(PokemonInfo.parse((PokemonListing) listing));
        }

        lore.addAll(ListingInfo.parse(listing));

        Button pokemon = GooeyButton.builder()
                .display(listing.getIcon())
                .with(DataComponents.CUSTOM_NAME, listing.getDisplayName())
                .with(DataComponents.LORE, new ItemLore(lore))
                .build();

        Button confirmList = GooeyButton.builder()
                .display(Gts.language.getPurchaseButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse("§2Confirm listing"))
                .onClick((action) -> {
                    boolean success = GtsAPI.addListing(listing, player, slot);

                    if (success) {
                        player.sendSystemMessage(Component.literal(Utils.formatPlaceholders(Gts.language.getListingSuccess(),
                                minPrice, speciesName, player.getDisplayName().getString(), null, price)));


                    } else {
                        player.sendSystemMessage(Component.literal(Utils.formatPlaceholders(Gts.language.getListingFail(),
                                minPrice, speciesName, player.getDisplayName().getString(), null, price)));
                    }

                    UIManager.closeUI(action.getPlayer());
                })
                .build();

        Button cancel = GooeyButton.builder()
                .display(Gts.language.getCancelButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse("§cCancel listing"))
                .onClick((action) -> {
                    UIManager.closeUI(action.getPlayer());
                })
                .build();

        ChestTemplate.Builder template = ChestTemplate.builder(3)
                .fill(Filler.getButton())
                .set(11, confirmList)
                .set(13, pokemon)
                .set(15, cancel);

        GooeyPage page = GooeyPage.builder()
                .template(template.build())
                .title("§cConfirmation - §r§f" + listing.getPriceAsString() + "\uE01A")
                .build();

        return page;
    }
}
