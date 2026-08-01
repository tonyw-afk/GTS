package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.item.PokemonItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.button.ManageListings;
import org.pokesplash.gts.UI.button.*;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.history.HistoryItem;
import org.pokesplash.gts.history.ItemHistoryItem;
import org.pokesplash.gts.history.PlayerHistory;
import org.pokesplash.gts.history.PokemonHistoryItem;
import org.pokesplash.gts.util.ColorUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * UI of the Manage Listings page.
 */
public class History {

	/**
	 * Method that returns the page.
	 * @return Listings page.
	 */
	public Page getPage(UUID owner, @NotNull Sort sort) {

		PlaceholderButton placeholder = new PlaceholderButton();

		PlayerHistory playerHistory = Gts.history.getPlayerHistory(owner);

		List<Button> buttons = new ArrayList<>();

		if (playerHistory != null) {
			// Gets all the items and sorts them by the sold date.
			List<HistoryItem> items = new ArrayList<>(playerHistory.getListings().stream().filter(historyItem -> {
				if(historyItem.getBuyerUuid() != null && historyItem.getBuyerUuid().equals(owner)) {
					return false;
				}

				if(historyItem.getBuyerName().equals(Gts.server.getPlayerList().getPlayer(owner))) {
					return false;
				}

				return true;
			}).toList());

			if(!items.isEmpty()) {
				items.sort(Comparator.comparing(HistoryItem::getSoldDate));
				Collections.reverse(items);

				// For each item, create a button.
				for (HistoryItem item : items) {

					// Standard lore for any item.
					List<Component> lore = new ArrayList<>();

					Button button = null;

					// Pokemon specific lore and button.
					if (item.isPokemon()) {
						PokemonHistoryItem pokemonItem = (PokemonHistoryItem) item;
						lore.addAll(PokemonInfo.parse(pokemonItem.getListing()));

						setHistoryLore(lore, item);

						button = GooeyButton.builder()
								.display(PokemonItem.from(pokemonItem.getListing(), 1))
								.with(DataComponents.CUSTOM_NAME, pokemonItem.getDisplayName())
								.with(DataComponents.LORE, new ItemLore(lore))
								.build();
					}
					// Item specific button.
					else {
						ItemHistoryItem itemHistoryItem = (ItemHistoryItem) item;

						if (itemHistoryItem.getListing() != null) {
							ItemLore itemLore = itemHistoryItem.getListing().get(DataComponents.LORE);

							if (itemLore != null) {
								lore.addAll(itemLore.lines());
							}

							setHistoryLore(lore, item);

							button = GooeyButton.builder()
									.display(itemHistoryItem.getListing())
									.with(DataComponents.CUSTOM_NAME, itemHistoryItem.getDisplayName())
									.with(DataComponents.LORE, new ItemLore(lore))
									//.with(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
									.build();
						}
					}

					// Adds the button to the list.
					if (button != null) {
						buttons.add(button);
					}
				}
			}
		}

		ChestTemplate template = ChestTemplate.builder(6)
				.rectangle(1, 0, 4, 9, placeholder)
				.fill(Filler.getButton())
				.set(0, SeePokemonListings.getButton(sort))
				.set(1, SeeItemListings.getButton(sort))
				.set(2, SeeAllListings.getButton(sort))

				.set(46, Barrier.getButton())
				.set(47, HistoryBoughtButton.getButton())
				.set(51, ManageListings.getButton())
				.set(52, RelistAll.getButton())

				.set(53, NextPage.getButton())
				.set(45, PreviousPage.getButton())
				.build();

		LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);

		page.setTitle(Gts.language.getHistoryTitle());

		setPageTitle(page);

		return page;
	}

	private void setPageTitle(LinkedPage page) {
		LinkedPage next = page.getNext();
		if (next != null) {
			next.setTitle(Gts.language.getHistoryTitle());
			setPageTitle(next);
		}
	}

	public void setHistoryLore(List<Component> lore, HistoryItem item) {
		lore.add(Component.literal(""));
		//lore.add(ColorUtil.parse(Gts.language.getSeller() + item.getSellerName()));
		lore.add(ColorUtil.parse(Gts.language.getPrice() + item.getPriceAsString()));
		lore.add(ColorUtil.parse(Gts.language.getBuyer() + item.getBuyerName()));

		String pattern = "d MMMM yyyy";
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		lore.add(ColorUtil.parse(Gts.language.getSold_date() +
				format.format(new Date(item.getSoldDate()))));
		lore.add(Component.literal(""));
	}
}
