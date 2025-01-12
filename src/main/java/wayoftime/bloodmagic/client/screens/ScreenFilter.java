package wayoftime.bloodmagic.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.common.item.inventory.ContainerFilter;
import wayoftime.bloodmagic.common.item.routing.IItemFilterProvider;
import wayoftime.bloodmagic.network.RouterFilterPacket;
import wayoftime.bloodmagic.util.GhostItemHelper;

public class ScreenFilter extends ScreenBase<ContainerFilter>
{
	private static final ResourceLocation background = BloodMagic.rl("textures/gui/routingfilter.png");
	public IInventory filterInventory;
	private PlayerEntity player;
	private int left, top;

	private TextFieldWidget textBox;

	public ScreenFilter(ContainerFilter container, PlayerInventory playerInventory, ITextComponent title)
	{
		super(container, playerInventory, title);
		filterInventory = container.inventoryFilter;
		xSize = 176;
		ySize = 187;
		this.player = playerInventory.player;
	}

	@Override
	public void init()
	{
		super.init();
		left = (this.width - this.xSize) / 2;
		top = (this.height - this.ySize) / 2;

		this.textBox = new TextFieldWidget(Minecraft.getInstance().fontRenderer, left + 23, top + 19, 70, 12, new StringTextComponent("itemGroup.search"));
		this.textBox.setEnableBackgroundDrawing(false);
//		this.textBox.setText("");
		this.textBox.setMaxStringLength(50);
		this.textBox.setVisible(true);
		this.textBox.setTextColor(16777215);
		this.textBox.setText("");

//        new TextFieldWidget(this.mc.fontRenderer, i + 25, j + 14, 80, 9 + 5, new TranslationTextComponent("itemGroup.search"));

//		this.buttons.clear();
////		this.buttons.add();
//		this.addButton(new Button(left + 135, top + 52, 14, 14, new StringTextComponent("D"), new DirectionalPress(tileTable, Direction.DOWN)));
//		this.addButton(new Button(left + 153, top + 52, 14, 14, new StringTextComponent("U"), new DirectionalPress(tileTable, Direction.UP)));
//		this.addButton(new Button(left + 135, top + 70, 14, 14, new StringTextComponent("N"), new DirectionalPress(tileTable, Direction.NORTH)));
//		this.addButton(new Button(left + 153, top + 70, 14, 14, new StringTextComponent("S"), new DirectionalPress(tileTable, Direction.SOUTH)));
//		this.addButton(new Button(left + 135, top + 88, 14, 14, new StringTextComponent("W"), new DirectionalPress(tileTable, Direction.WEST)));
//		this.addButton(new Button(left + 153, top + 88, 14, 14, new StringTextComponent("E"), new DirectionalPress(tileTable, Direction.EAST)));
	}

//	public boolean charTyped(char codePoint, int modifiers) {
//	      if (this.field_199738_u) {
//	         return false;
//	      } else if (this.isVisible() && !this.mc.player.isSpectator()) {
//	         if (this.searchBar.charTyped(codePoint, modifiers)) {
//	            this.updateSearch();
//	            return true;
//	         } else {
//	            return IGuiEventListener.super.charTyped(codePoint, modifiers);
//	         }
//	      } else {
//	         return false;
//	      }
//	   }

	@Override
	public void tick()
	{
		super.tick();
		this.textBox.tick();

	}

	int backSpaceKey = 259;

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (this.textBox.isFocused())
		{
			if (keyCode == backSpaceKey && container.lastGhostSlotClicked != -1)
			{
				String str = this.textBox.getText();

				if (str != null && str.length() > 0)
				{
					str = str.substring(0, str.length() - 1);
					this.textBox.setText(str);
					int amount = 0;
					if (str.length() > 0)
					{
						try
						{
							Integer testVal = Integer.decode(str);
							if (testVal != null)
							{
								amount = testVal;
							}
						} catch (NumberFormatException d)
						{
						}
					}

					setValueOfGhostItemInSlot(container.lastGhostSlotClicked, amount);
				}
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode)
	{
		if (this.textBox.charTyped(typedChar, keyCode))
		{
			if (container.lastGhostSlotClicked != -1)
			{
				String str = this.textBox.getText();
				int amount = 0;

				if (!str.isEmpty())
				{
					try
					{
						Integer testVal = Integer.decode(str);
						if (testVal != null)
						{
							amount = testVal;
						}
					} catch (NumberFormatException d)
					{
					}
				}

				setValueOfGhostItemInSlot(container.lastGhostSlotClicked, amount);
			}
			return true;
		} else
		{
			return super.charTyped(typedChar, keyCode);
		}
	}

	private void setValueOfGhostItemInSlot(int ghostItemSlot, int amount)
	{
		Slot slot = container.getSlot(ghostItemSlot);
		ItemStack ghostStack = slot.getStack();
//		ItemStack ghostStack = container.inventoryFilter.getStackInSlot(ghostItemSlot);
		if (!ghostStack.isEmpty())
		{
			GhostItemHelper.setItemGhostAmount(ghostStack, amount);
			GhostItemHelper.setItemGhostAmount(container.inventoryFilter.getStackInSlot(ghostItemSlot), amount);
			if (container.filterStack.getItem() instanceof IItemFilterProvider)
			{
				((IItemFilterProvider) container.filterStack.getItem()).setGhostItemAmount(container.filterStack, ghostItemSlot, amount);

			}
		}

		BloodMagic.packetHandler.sendToServer(new RouterFilterPacket(player.inventory.currentItem, ghostItemSlot, amount));
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		boolean testBool = super.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.textBox.mouseClicked(mouseX, mouseY, mouseButton))
		{
			return true;
		}

		if (container.lastGhostSlotClicked != -1)
		{
			Slot slot = container.getSlot(container.lastGhostSlotClicked);
			ItemStack stack = slot.getStack();
			if (!stack.isEmpty())
			{
				int amount = GhostItemHelper.getItemGhostAmount(stack);
				if (amount == 0)
				{
					this.textBox.setText("");
				} else
				{
					this.textBox.setText("" + amount);
				}
			} else
			{
				this.textBox.setText("");
			}
		}

		System.out.println(testBool);
		return true;
	}

	@Override
	public ResourceLocation getBackground()
	{
		return background;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY)
	{
//		this.font.func_243248_b(stack, new TranslationTextComponent("tile.bloodmagic.alchemytable.name"), 8, 5, 4210752);
//		this.font.func_243248_b(stack, new TranslationTextComponent("container.inventory"), 8, 111, 4210752);
		this.font.func_243248_b(stack, new TranslationTextComponent("item.bloodmagic.sigilofholding"), 53, 4, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		getMinecraft().getTextureManager().bindTexture(background);
//		int i = (this.width - this.xSize) / 2;
//		int j = (this.height - this.ySize) / 2;
//		this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
//
//		int l = this.getCookProgressScaled(90);
//		this.blit(stack, i + 115, j + 14 + 90 - l, 176, 90 - l, 18, l);
//
//		for (int slotId = 0; slotId < 6; slotId++)
//		{
//			if (!((TileAlchemyTable) filterInventory).isInputSlotAccessible(slotId))
//			{
//				Slot slot = this.getContainer().getSlot(slotId);
//
//				this.blit(stack, i + slot.xPos, j + slot.yPos, 195, 1, 16, 16);
//			}
//		}

		// draw your Gui here, only thing you need to change is the path
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.blit(stack, x, y, 0, 0, xSize, ySize);
		ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
//		if (!held.isEmpty() && held.getItem() == BloodMagicItems.HOLDING_SIGIL.get())
//		{
////            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//			this.blit(stack, 4 + x + 36 * ItemSigilHolding.getCurrentItemOrdinal(player.getHeldItemMainhand()), y + 13, 0, 123, 24, 24);
//		}

	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		{
			this.textBox.render(matrixStack, mouseX, mouseY, partialTicks);
		}

	}

//

}
