package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.UI.button.ManageListings;
import org.pokesplash.gts.UI.button.*;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * UI of the Item Listings page.
 */
public class ItemListings {

    /**
     * Method that returns the page.
     *
     * @return Item Listings page.
     */
    public Page getPage(@NotNull Sort sort) {

        List<ItemListing> itmListings = Gts.listings.getItemListings();

        if (sort.equals(Sort.PRICE)) {
            itmListings.sort(Comparator.comparingDouble(ItemListing::getPrice));
        } else if (sort.equals(Sort.PRICE_REVERSED)) {
            itmListings.reversed().sort(Comparator.comparingDouble(ItemListing::getPrice));
        }

        if (sort.equals(Sort.DATE)) {
            itmListings.sort(Comparator.comparingLong(ItemListing::getEndTime));
        } else if (sort.equals(Sort.DATE_REVERSED)) {
            itmListings.reversed().sort(Comparator.comparingLong(ItemListing::getEndTime));
        }

        if (sort.equals(Sort.NAME)) {
            itmListings.sort(Comparator.comparing(itemListing -> itemListing.getDisplayName().getString()));
        } else if (sort.equals(Sort.NAME_REVERSED)) {
            itmListings.reversed().sort(Comparator.comparing(itemListing -> itemListing.getDisplayName().getString()));
        }

        Button sortByPriceButton = GooeyButton.builder()
                .display(Gts.language.getSortByPriceButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse(Gts.language.getSortByPriceButtonLabel()))
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();

                    Page page;
                    if (sort.equals(Sort.PRICE)) {
                        page = new ItemListings().getPage(Sort.PRICE_REVERSED);
                    } else {
                        page = new ItemListings().getPage(Sort.PRICE);
                    }

                    UIManager.openUIForcefully(sender, page);
                })
                .build();

        Button sortByNewestButton = GooeyButton.builder()
                .display(Gts.language.getSortByNewestButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse(Gts.language.getSortByNewestButtonLabel()))
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();
                    Page page;
                    if (sort.equals(Sort.DATE)) {
                        page = new ItemListings().getPage(Sort.DATE_REVERSED);
                    } else {
                        page = new ItemListings().getPage(Sort.DATE);
                    }
                    UIManager.openUIForcefully(sender, page);
                })
                .build();

        Button sortByNameButton = GooeyButton.builder()
                .display(Gts.language.getSortByNameButtonItem())
                .with(DataComponents.CUSTOM_NAME,
                        ColorUtil.parse(Gts.language.getSortByNameButtonLabel()))
                .onClick((action) -> {
                    ServerPlayer sender = action.getPlayer();
                    Page page;
                    if (sort.equals(Sort.NAME)) {
                        page = new ItemListings().getPage(Sort.NAME_REVERSED);
                    } else {
                        page = new ItemListings().getPage(Sort.NAME);
                    }
                    UIManager.openUIForcefully(sender, page);
                })
                .build();

        PlaceholderButton placeholder = new PlaceholderButton();

        List<Button> itemButtons = new ArrayList<>();
        for (ItemListing listing : itmListings) {
            List<Component> lore = ListingInfo.parse(listing);

            Button button = GooeyButton.builder()
                    .display(listing.getListing())
                    .with(DataComponents.CUSTOM_NAME, listing.getDisplayName())
                    .with(DataComponents.LORE, new ItemLore(lore))
                    .with(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
                    .onClick((action) -> {
                        ServerPlayer sender = action.getPlayer();
                        Page page = new SingleListing().getPage(sender, listing);
                        UIManager.openUIForcefully(sender, page);
                    })
                    .build();
            itemButtons.add(button);
        }

        ChestTemplate template = ChestTemplate.builder(6)
                .rectangle(1, 0, 4, 9, placeholder)
                .fill(Filler.getButton())
                .set(0, SeePokemonListings.getButton(sort))
                .set(1, Barrier.getButton())
                .set(2, SeeAllListings.getButton(sort))

                .set(6, sortByPriceButton)
                .set(7, sortByNewestButton)
                .set(8, sortByNameButton)

                .set(45, PreviousPage.getButton())
                .set(53, NextPage.getButton())

                .set(51, ManageListings.getButton())
                .set(52, RelistAll.getButton())
                .build();

        LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, itemButtons, null);
        page.setTitle(Gts.language.getItemListingsTitle());

        setPageTitle(page);

        return page;
    }

    private void setPageTitle(LinkedPage page) {
        LinkedPage next = page.getNext();
        if (next != null) {
            next.setTitle(Gts.language.getItemListingsTitle());
            setPageTitle(next);
        }
    }
}
