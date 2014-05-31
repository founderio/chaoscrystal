package founderio.chaoscrystal.aspects;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.util.GeometryHelper;
import founderio.util.ItemUtil;
import founderio.util.ListUtil;

public class DegradationHelper {
	public static boolean isAspectStoreEmpty(NBTTagCompound aspectStore) {
		if (aspectStore == null) {
			return true;
		}
		boolean hasAspects = false;
		for (String aspect : Aspects.ASPECTS) {
			int asp = aspectStore.getInteger(aspect);
			if (asp > 0) {
				hasAspects = true;
				break;
			}
		}
		return !hasAspects;
	}

	public static void processReplacement(World world, int posX, int posY,
			int posZ, ItemStack[] replacement) {

		if (replacement.length == 1
				&& replacement[0].getItem() instanceof ItemBlock) {
			world.setBlock(posX, posY, posZ, Block.getBlockFromItem(replacement[0].getItem()), replacement[0]
							.getItemDamage(), 1 + 2);

		} else {
			world.setBlock(posX, posY, posZ, Blocks.air, 0, 1 + 2);
			spawnMultiplesOfStacks(replacement, 1, world, posX, posY, posZ);
		}
	}
	
	public static void spawnMultiplesOfStacks(ItemStack[] nodes, int count,
			World world, EntityItem reference) {
		for (int i = 0; i < nodes.length; i++) {
			ItemStack p = nodes[i];
			if (p.getItem() == null || p.stackSize == 0) {
				continue;
			}
			int maxStackSize = p.getMaxStackSize();
			for (int ists = 0; ists < count / maxStackSize; ists++) {
				ItemStack spawnStack = p.copy();
				spawnStack.stackSize = maxStackSize;
				ItemUtil.spawnItemStack(spawnStack, world, reference);
			}
			ItemStack spawnStack = p.copy();
			spawnStack.stackSize = count % maxStackSize;
			ItemUtil.spawnItemStack(spawnStack, world, reference);
		}
	}

	public static void spawnMultiplesOfStacks(ItemStack[] nodes, int count,
			World world, int posX, int posY, int posZ) {
		for (int i = 0; i < nodes.length; i++) {
			ItemStack p = nodes[i];
			if (p.getItem() == null || p.stackSize == 0) {
				continue;
			}
			int maxStackSize = p.getMaxStackSize();
			for (int ists = 0; ists < count / maxStackSize; ists++) {
				ItemStack spawnStack = p.copy();
				spawnStack.stackSize = maxStackSize;
				ItemUtil.spawnItemStack(spawnStack, world, posX + 0.5,
						posY + 0.1, posZ + 0.5);
			}
			ItemStack spawnStack = p.copy();
			spawnStack.stackSize = count % maxStackSize;
			ItemUtil.spawnItemStack(spawnStack, world, posX + 0.5, posY + 0.1,
					posZ + 0.5);
		}
	}

	public static void crystalTick(EntityChaosCrystal entity, World world,
			double posX, double posY, double posZ, List<String> filterAspects, List<String> filterTargets, double range,
			boolean extract) {

		boolean allowBlocks = filterTargets.isEmpty() || filterTargets.contains(Targets.TARGET_ALL) || filterTargets.contains(Targets.TARGET_BLOCKS);
		boolean allowItems = filterTargets.isEmpty() || filterTargets.contains(Targets.TARGET_ALL) || filterTargets.contains(Targets.TARGET_ITEMS);

		int hit = 0;
		int tries = 0;

		if(allowBlocks) {
			do {
				tries++;
				float offX = ChaosCrystalMain.rand.nextInt((int) (range * 2))
						- (float) range + 0.5f;
				float offY = ChaosCrystalMain.rand.nextInt((int) (range * 2))
						- (float) range + 0.5f;
				float offZ = ChaosCrystalMain.rand.nextInt((int) (range * 2))
						- (float) range + 0.5f;

				if (Math.sqrt(offX * offX + offY * offY + offZ * offZ) < range) {
					int absX = (int) (posX + offX);
					int absY = (int) (posY + offY);
					int absZ = (int) (posZ + offZ);

					Block id = world.getBlock(absX, absY, absZ);

					if (!id.isAir(world, absX, absY, absZ)
							&& !ChaosCrystalMain.degradationStore.isIgnoreBlock(id, extract)) {// We can't extract air...
						
						int meta = world.getBlockMetadata(absX, absY, absZ);
						ItemStack is = new ItemStack(id, 1, meta);
						
						Node degradation = getDegradation(is, extract);
						ItemStack[] results = getDegradationResults(degradation, is, extract);
						
						if (results == null || results.length == 0) {
							continue;
						}
						int[] aspects = degradation.getAspectDifference();

						if (!Aspects.isFilterMatched(filterAspects, aspects)) {
							continue;
						}

						if (extract) {
							if (entity.canAcceptAspects(aspects)) {
								hit++;

								entity.addAspects(aspects);
								processReplacement(world, absX, absY, absZ,
										results);

								CommonProxy.spawnParticleEffects(
										world.provider.dimensionId, 0,
										absX, absY, absZ, posX, posY, posZ);
								CommonProxy.spawnParticleEffects(
										world.provider.dimensionId, 2,
										absX, absY, absZ);
							}
						} else {
							if (entity.canProvideAspects(aspects)) {
								hit++;

								entity.subtractAspects(aspects);
								processReplacement(world, absX, absY, absZ,
										results);

								CommonProxy.spawnParticleEffects(
										world.provider.dimensionId, 0,
										posX, posY, posZ, absX, absY, absZ);
								CommonProxy.spawnParticleEffects(
										world.provider.dimensionId, 2,
										absX, absY, absZ);
							}
						}
					}
				}
			} while (hit < ChaosCrystalMain.cfgHitsPerTick
					&& tries < ChaosCrystalMain.cfgMaxTriesPerTick);
		}

		if(allowItems) {
			if (hit < ChaosCrystalMain.cfgHitsPerTick) {
				List<EntityItem> items = GeometryHelper.getEntitiesInRange(world, posX, posY, posZ, range, EntityItem.class);
				
				if (items.size() != 0) {

					EntityItem it = ListUtil.getRandomFromList(items, ChaosCrystalMain.rand);
					ItemStack is = it.getEntityItem();
					
					Node degradation = getDegradation(is, extract);
					ItemStack[] results = getDegradationResults(degradation, is, extract);

					if (results == null || results.length == 0) {
						// continue;
					} else {
						int[] aspects = degradation.getAspectDifference();
	
						if (Aspects.isFilterMatched(filterAspects, aspects)) {
							int count = 0;
							int size = ChaosCrystalMain.cfgHitsPerTick - hit;
							// TODO: Additionally limit stack size with random
							// number, so higher max hit does not cause while
							// stacks to get processed at once
							if(is.stackSize < size) {
								size = is.stackSize;
							}
							
							if (extract) {
								for (int st = 0; st < size; st++) {
									if (entity.canAcceptAspects(aspects)) {
										count++;
	
										entity.addAspects(aspects);
									} else {
										break;
									}
								}
							} else {
								for (int st = 0; st < size; st++) {
									if (entity.canProvideAspects(aspects)) {
										count++;
	
										entity.subtractAspects(aspects);
									} else {
										break;
									}
								}
							}
							hit += count;
							if (count > 0) {
								is.stackSize -= count;
								
								spawnMultiplesOfStacks(results, count, world, it);
	
								if (is.stackSize == 0) {
									it.setDead();
								} else {
									it.setEntityItemStack(is);
								}
								if(extract) {
									CommonProxy.spawnParticleEffects(it, entity, 0);
								} else {
									CommonProxy.spawnParticleEffects(entity, it, 0);
								}
								CommonProxy.spawnParticleEffects(it, 2);
							}
						}
					}
				}
			}
		}
		if (hit > 0) {
			entity.playSound("mob.enderdragon.growl", 0.1f, 0.1f);
		}
	}
	
	public static Node getDegradation(ItemStack is, boolean extract) {
		if(is == null) {
			return null;
		}
		if(ChaosCrystalMain.degradationStore.isIgnoreItem(is.getItem(), extract)) {
			return null;
		}
		List<Node> nodes;
		if (extract) {
			nodes = ChaosCrystalMain.degradationStore
					.getExtractionsFrom(is);
		} else {
			nodes = ChaosCrystalMain.degradationStore
					.getInfusionsFrom(is);
		}
		return ListUtil.getRandomFromList(nodes,
				ChaosCrystalMain.rand);
	}
	
	public static ItemStack[] getDegradationResults(Node node, ItemStack is, boolean extract) {

		if (node == null) {
			if (ChaosCrystalMain.cfgDebugOutput) {
				System.out.println(is.getDisplayName() + " - "
						+ is.getItem() + "/" + is.getItemDamage());
			}
			return null;
		}
		ItemStack[] results;
		if (extract) {
			results = node.getDegradedFrom(new ItemStack(is.getItem(), 1, is.getItemDamage()));
		} else {
			results = new ItemStack[] { node.getDispayItemStack() };
		}
		return results;
	}

	public static boolean canAcceptAspects(String[] aspects, int[] amounts,
			IAspectStore aspectStore) {
		for (int a = 0; a < aspects.length; a++) {
			if (aspectStore.getAspect(aspects[a]) + amounts[a] > ChaosCrystalMain.cfgCrystalAspectStorage) {
				return false;
			}
		}
		return true;
	}

	public static boolean canProvideAspects(String[] aspects, int[] amounts,
			IAspectStore aspectStore) {
		for (int a = 0; a < aspects.length; a++) {
			if (aspectStore.getAspect(aspects[a]) < amounts[a]) {
				return false;
			}
		}
		return true;
	}
}
