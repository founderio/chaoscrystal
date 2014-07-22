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
import founderio.chaoscrystal.Config;
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
		for (Aspect aspect : Aspect.values()) {
			int asp = aspectStore.getInteger(aspect.stringRep);
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
			double posX, double posY, double posZ, List<Aspect> filterAspects, List<String> filterTargets, double range,
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
							&& !ChaosCrystalMain.chaosRegistry.isIgnoreBlock(id, extract)) {// We can't extract air...

						int meta = world.getBlockMetadata(absX, absY, absZ);
						ItemStack is = new ItemStack(id, 1, meta);

						Node degradation = getDegradation(is, extract);
						ItemStack results = getDegradationResult(degradation, extract);

						if (results == null) {
							continue;
						}
						int[] aspects = degradation.getAspects();

						if (!Aspect.isFilterMatched(filterAspects, aspects)) {
							continue;
						}

						if (extract) {
							if (entity.canAcceptAspects(aspects)) {
								hit++;

								entity.addAspects(aspects);
								processReplacement(world, absX, absY, absZ,
										new ItemStack[] {results});

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
										new ItemStack[] {results});

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
			} while (hit < Config.cfgHitsPerTick
					&& tries < Config.cfgMaxTriesPerTick);
		}

		if(allowItems) {
			if (hit < Config.cfgHitsPerTick) {
				List<EntityItem> items = GeometryHelper.getEntitiesInRange(world, posX, posY, posZ, range, EntityItem.class);

				if (items.size() != 0) {

					EntityItem it = ListUtil.getRandomFromList(items, ChaosCrystalMain.rand);
					ItemStack is = it.getEntityItem();

					Node degradation = getDegradation(is, extract);
					ItemStack result = getDegradationResult(degradation, extract);

					if (result == null) {
						// continue;
					} else {
						int[] aspects = degradation.getAspects();

						if (Aspect.isFilterMatched(filterAspects, aspects)) {
							int count = 0;
							// Limit max amount of processed items by max hits and stack size
							int size = Config.cfgHitsPerTick - hit;
							if(is.stackSize < size) {
								size = is.stackSize;
							}
							if(size > 0) {
								// Additionally limits stack size with random
								// number, so higher max hit does not cause while
								// stacks to get processed at once:
								size = ChaosCrystalMain.rand.nextInt(size) + 1;

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

									//TODO: non-erray variant of spawnMultiplesOfStacks
									spawnMultiplesOfStacks(new ItemStack[] {result}, count, world, it);

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
		}
		if (hit > 0) {
			entity.playSound("mob.enderdragon.growl", 0.1f, 0.1f);
		}
	}

	public static Node getDegradation(ItemStack is, boolean extract) {
		if(is == null) {
			return null;
		}
		if(ChaosCrystalMain.chaosRegistry.isIgnoreItem(is.getItem(), extract)) {
			return null;
		}
		List<Node> nodes;
		if (extract) {
			nodes = ChaosCrystalMain.chaosRegistry.getExtractionsFrom(is);
		} else {
			nodes = ChaosCrystalMain.chaosRegistry.getInfusionsFrom(is);
		}
		return ListUtil.getRandomFromList(nodes,
				ChaosCrystalMain.rand);
	}

	public static ItemStack getDegradationResult(Node node, boolean extract) {
		if (node == null) {
			return null;
		}
		ItemStack result;
		if (extract) {
			result = node.getLesser().createItemStack();
		} else {
			result = node.getGreater().createItemStack();
		}
		return result;
	}

	public static boolean canAcceptAspects(Aspect[] aspects, int[] amounts,
			IAspectStore aspectStore) {
		for (int a = 0; a < aspects.length; a++) {
			if (aspectStore.getAspect(aspects[a]) + amounts[a] > Config.cfgCrystalAspectStorage) {
				return false;
			}
		}
		return true;
	}

	public static boolean canProvideAspects(Aspect[] aspects, int[] amounts,
			IAspectStore aspectStore) {
		for (int a = 0; a < aspects.length; a++) {
			if (aspectStore.getAspect(aspects[a]) < amounts[a]) {
				return false;
			}
		}
		return true;
	}
}
