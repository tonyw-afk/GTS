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
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.ManageListings;
import org.pokesplash.gts.UI.button.*;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.api.provider.ListingAPI;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * UI of the Pokemon Listings page.
 */
public class AllListings {

	/**
	 * Method that returns the page.
	 * @return Pokemon Listings page.
	 */
	public Page getPage(@NotNull Sort sort) {

		PlaceholderButton placeholder = new PlaceholderButton();

		List<Button> buttons = new ArrayList<>();

		List<Listing> listings = ListingAPI.getHighestPriority() == null ? Gts.listings.getListings() :
				Gts.listings.getListings().stream().map(Listing::deepClone).toList();

		listings.reversed().sort(Comparator.comparingLong(Listing::getEndTime));

		if (sort.equals(Sort.PRICE)) {
			listings.sort(Comparator.comparingDouble(Listing::getPrice));
		} else if (sort.equals(Sort.PRICE_REVERSED)) {
			listings.reversed().sort(Comparator.comparingDouble(Listing::getPrice));
		}

		if (sort.equals(Sort.DATE)) {
			listings.sort(Comparator.comparingLong(Listing::getEndTime));
		} else if (sort.equals(Sort.DATE_REVERSED)) {
			listings.reversed().sort(Comparator.comparingLong(Listing::getEndTime));
		}

		if (sort.equals(Sort.NAME)) {
			listings.sort(Comparator.comparing(listing -> listing.getDisplayName().getString()));
		} else if (sort.equals(Sort.NAME_REVERSED)) {
			listings.reversed().sort(Comparator.comparing(listing -> listing.getDisplayName().getString()));
		}

		Button sortByPriceButton = GooeyButton.builder()
				.display(Gts.language.getSortByPriceButtonItem())
				.with(DataComponents.CUSTOM_NAME,
						ColorUtil.parse(Gts.language.getSortByPriceButtonLabel()))
				.onClick((action) -> {
					ServerPlayer sender = action.getPlayer();
					Page page;
					if (sort.equals(Sort.PRICE)) {
						page = new AllListings().getPage(Sort.PRICE_REVERSED);
					} else {
						page = new AllListings().getPage(Sort.PRICE);
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
						page = new AllListings().getPage(Sort.DATE_REVERSED);
					} else {
						page = new AllListings().getPage(Sort.DATE);
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
						page = new AllListings().getPage(Sort.NAME_REVERSED);
					} else {
						page = new AllListings().getPage(Sort.NAME);
					}
					UIManager.openUIForcefully(sender, page);
				})
				.build();

		for (Listing listing : listings) {
			List<Component> lore = ListingInfo.parse(listing);

			if (listing.isPokemon()) {
				lore.addAll(PokemonInfo.parse((PokemonListing) listing));
			}

			Button button = GooeyButton.builder()
					.display(listing.getIcon())
					.with(DataComponents.CUSTOM_NAME, listing.getDisplayName())
					.with(DataComponents.LORE, new ItemLore(lore))
					.onClick((action) -> {
						ServerPlayer sender = action.getPlayer();
						Page page = new SingleListing().getPage(sender, listing);
						UIManager.openUIForcefully(sender, page);
					})
					.build();

			buttons.add(button);
		}

		ChestTemplate template = ChestTemplate.builder(6)
				.rectangle(1, 0, 4, 9, placeholder)
				.fill(Filler.getButton())
				.set(0, SeePokemonListings.getButton(sort))
				.set(1, SeeItemListings.getButton(sort))

				.set(6, sortByPriceButton)
				.set(7, sortByNewestButton)
				.set(8, sortByNameButton)

				.set(45, PreviousPage.getButton())
				.set(53, NextPage.getButton())

				.set(51, ManageListings.getButton())
				.set(52, RelistAll.getButton())
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
		page.setTitle(Gts.language.getTitle());

		setPageTitle(page);

		return page;
	}

	private void setPageTitle(LinkedPage page) {
		LinkedPage next = page.getNext();
		if (next != null) {
			next.setTitle(Gts.language.getTitle());
			setPageTitle(next);
		}
	}
}
