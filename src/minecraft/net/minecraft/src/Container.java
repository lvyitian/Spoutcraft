package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Container {
	public List inventoryItemStacks = new ArrayList();
	public List inventorySlots = new ArrayList();
	public int windowId = 0;
	private short transactionID = 0;
	protected List crafters = new ArrayList();
	private Set field_20918_b = new HashSet();
	
	//Spout start
	public abstract IInventory getInventory();
	//Spout end

	protected void addSlot(Slot par1Slot) {
		par1Slot.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(par1Slot);
		this.inventoryItemStacks.add((Object)null);
	}

	public void updateCraftingResults() {
		for (int var1 = 0; var1 < this.inventorySlots.size(); ++var1) {
			ItemStack var2 = ((Slot)this.inventorySlots.get(var1)).getStack();
			ItemStack var3 = (ItemStack)this.inventoryItemStacks.get(var1);
			if (!ItemStack.areItemStacksEqual(var3, var2)) {
				var3 = var2 == null?null:var2.copy();
				this.inventoryItemStacks.set(var1, var3);

				for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
					((ICrafting)this.crafters.get(var4)).updateCraftingInventorySlot(this, var1, var3);
				}
			}
		}
	}

	public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
		return false;
	}

	public Slot getSlot(int par1) {
		return (Slot)this.inventorySlots.get(par1);
	}

	public ItemStack transferStackInSlot(int par1) {
		Slot var2 = (Slot)this.inventorySlots.get(par1);
		return var2 != null?var2.getStack():null;
	}

	public ItemStack slotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {
		ItemStack var5 = null;
		if (par2 > 1) {
			return null;
		} else {
			if (par2 == 0 || par2 == 1) {
				InventoryPlayer var6 = par4EntityPlayer.inventory;
				if (par1 == -999) {
					if (var6.getItemStack() != null && par1 == -999) {
						if (par2 == 0) {
							par4EntityPlayer.dropPlayerItem(var6.getItemStack());
							var6.setItemStack((ItemStack)null);
						}

						if (par2 == 1) {
							par4EntityPlayer.dropPlayerItem(var6.getItemStack().splitStack(1));
							if (var6.getItemStack().stackSize == 0) {
								var6.setItemStack((ItemStack)null);
							}
						}
					}
				} else if (par3) {
					ItemStack var7 = this.transferStackInSlot(par1);
					if (var7 != null) {
						int var8 = var7.itemID;
						var5 = var7.copy();
						Slot var9 = (Slot)this.inventorySlots.get(par1);
						if (var9 != null && var9.getStack() != null && var9.getStack().itemID == var8) {
							this.retrySlotClick(par1, par2, par3, par4EntityPlayer);
						}
					}
				} else {
					if (par1 < 0) {
						return null;
					}

					Slot var12 = (Slot)this.inventorySlots.get(par1);
					if (var12 != null) {
						var12.onSlotChanged();
						ItemStack var13 = var12.getStack();
						ItemStack var14 = var6.getItemStack();
						if (var13 != null) {
							var5 = var13.copy();
						}

						int var10;
						if (var13 == null) {
							if (var14 != null && var12.isItemValid(var14)) {
								var10 = par2 == 0?var14.stackSize:1;
								if (var10 > var12.getSlotStackLimit()) {
									var10 = var12.getSlotStackLimit();
								}

								var12.putStack(var14.splitStack(var10));
								if (var14.stackSize == 0) {
									var6.setItemStack((ItemStack)null);
								}
							}
						} else if (var14 == null) {
							var10 = par2 == 0?var13.stackSize:(var13.stackSize + 1) / 2;
							ItemStack var11 = var12.decrStackSize(var10);
							var6.setItemStack(var11);
							if (var13.stackSize == 0) {
								var12.putStack((ItemStack)null);
							}

							var12.onPickupFromSlot(var6.getItemStack());
						} else if (var12.isItemValid(var14)) {
							if (var13.itemID == var14.itemID && (!var13.getHasSubtypes() || var13.getItemDamage() == var14.getItemDamage()) && ItemStack.func_46154_a(var13, var14)) {
								var10 = par2 == 0?var14.stackSize:1;
								if (var10 > var12.getSlotStackLimit() - var13.stackSize) {
									var10 = var12.getSlotStackLimit() - var13.stackSize;
								}

								if (var10 > var14.getMaxStackSize() - var13.stackSize) {
									var10 = var14.getMaxStackSize() - var13.stackSize;
								}

								var14.splitStack(var10);
								if (var14.stackSize == 0) {
									var6.setItemStack((ItemStack)null);
								}

								var13.stackSize += var10;
							} else if (var14.stackSize <= var12.getSlotStackLimit()) {
								var12.putStack(var14);
								var6.setItemStack(var13);
							}
						} else if (var13.itemID == var14.itemID && var14.getMaxStackSize() > 1 && (!var13.getHasSubtypes() || var13.getItemDamage() == var14.getItemDamage()) && ItemStack.func_46154_a(var13, var14)) {
							var10 = var13.stackSize;
							if (var10 > 0 && var10 + var14.stackSize <= var14.getMaxStackSize()) {
								var14.stackSize += var10;
								var13 = var12.decrStackSize(var10);
								if (var13.stackSize == 0) {
									var12.putStack((ItemStack)null);
								}

								var12.onPickupFromSlot(var6.getItemStack());
							}
						}
					}
				}
			}

			return var5;
		}
	}

	protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {
		this.slotClick(par1, par2, par3, par4EntityPlayer);
	}

	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		InventoryPlayer var2 = par1EntityPlayer.inventory;
		if (var2.getItemStack() != null) {
			par1EntityPlayer.dropPlayerItem(var2.getItemStack());
			var2.setItemStack((ItemStack)null);
		}
	}

	public void onCraftMatrixChanged(IInventory par1IInventory) {
		this.updateCraftingResults();
	}

	public void putStackInSlot(int par1, ItemStack par2ItemStack) {
		this.getSlot(par1).putStack(par2ItemStack);
	}

	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		for (int var2 = 0; var2 < par1ArrayOfItemStack.length; ++var2) {
			this.getSlot(var2).putStack(par1ArrayOfItemStack[var2]);
		}
	}

	public void updateProgressBar(int par1, int par2) {}

	public short getNextTransactionID(InventoryPlayer par1InventoryPlayer) {
		++this.transactionID;
		return this.transactionID;
	}

	public void func_20113_a(short par1) {}

	public void func_20110_b(short par1) {}

	public abstract boolean canInteractWith(EntityPlayer var1);

	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4) {
		boolean var5 = false;
		int var6 = par2;
		if (par4) {
			var6 = par3 - 1;
		}

		Slot var7;
		ItemStack var8;
		if (par1ItemStack.isStackable()) {
			while (par1ItemStack.stackSize > 0 && (!par4 && var6 < par3 || par4 && var6 >= par2)) {
				var7 = (Slot)this.inventorySlots.get(var6);
				var8 = var7.getStack();
				if (var8 != null && var8.itemID == par1ItemStack.itemID && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == var8.getItemDamage()) && ItemStack.func_46154_a(par1ItemStack, var8)) {
					int var9 = var8.stackSize + par1ItemStack.stackSize;
					if (var9 <= par1ItemStack.getMaxStackSize()) {
						par1ItemStack.stackSize = 0;
						var8.stackSize = var9;
						var7.onSlotChanged();
						var5 = true;
					} else if (var8.stackSize < par1ItemStack.getMaxStackSize()) {
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - var8.stackSize;
						var8.stackSize = par1ItemStack.getMaxStackSize();
						var7.onSlotChanged();
						var5 = true;
					}
				}

				if (par4) {
					--var6;
				} else {
					++var6;
				}
			}
		}

		if (par1ItemStack.stackSize > 0) {
			if (par4) {
				var6 = par3 - 1;
			} else {
				var6 = par2;
			}

			while (!par4 && var6 < par3 || par4 && var6 >= par2) {
				var7 = (Slot)this.inventorySlots.get(var6);
				var8 = var7.getStack();
				if (var8 == null) {
					var7.putStack(par1ItemStack.copy());
					var7.onSlotChanged();
					par1ItemStack.stackSize = 0;
					var5 = true;
					break;
				}

				if (par4) {
					--var6;
				} else {
					++var6;
				}
			}
		}

		return var5;
	}
}