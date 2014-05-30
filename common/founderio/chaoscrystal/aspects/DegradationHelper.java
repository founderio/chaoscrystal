package founderio.chaoscrystal.aspects;

import java.util.ArrayList;
import java.util.Arrays;
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
import founderio.chaoscrystal.aspects.modules.ModuleVanillaWorldgen;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
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

	public static void spawnMultiplesOfNodes(Node[] nodes, int count,
			World world, EntityItem reference) {
		for (int i = 0; i < nodes.length; i++) {
			Node p = nodes[i];
			if (p == ModuleVanillaWorldgen.AIR) {
				continue;
			}
			int maxStackSize = p.getDispayItemStack().getMaxStackSize();
			for (int ists = 0; ists < count / maxStackSize; ists++) {
				ItemStack spawnStack = p.getDispayItemStack().copy();
				spawnStack.stackSize = maxStackSize;
				ItemUtil.spawnItemStack(spawnStack, world, reference);
			}
			ItemStack spawnStack = p.getDispayItemStack().copy();
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
			int posX, int posY, int posZ, List<String> filterAspects, List<String> filterTargets, double range,
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
							&& (!extract || id != ChaosCrystalMain.blockLifeless)) {// We can't extract air...
						
						int meta = world.getBlockMetadata(absX, absY, absZ);
						List<Node> nodes;
						if (extract) {
							nodes = ChaosCrystalMain.degradationStore
									.getExtractionsFrom(new ItemStack(id, 1, meta));
						} else {
							nodes = ChaosCrystalMain.degradationStore
									.getInfusionsFrom(new ItemStack(id, 1, meta));
						}

						Node degradation = ListUtil.getRandomFromList(nodes,
								ChaosCrystalMain.rand);

						if (degradation != null) {
							ItemStack[] results;
							if (extract) {
								results = degradation.getDegradedFrom(new ItemStack(id, 1, meta));
							} else {
								results = new ItemStack[] { degradation.getDispayItemStack() };
							}
							if (results.length == 0) {
								continue;
							}
							int[] aspects = degradation.getAspectDifference();

							if (!filterAspects.isEmpty()
									&& !filterAspects.containsAll(Arrays.asList(Aspects
											.getAspectNames(aspects)))) {
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
								}
							}

						} else {
							if (ChaosCrystalMain.cfgDebugOutput) {
								System.out.println(id.getLocalizedName()
										+ " - " + id + "/" + meta);
							}
							if (extract && !ChaosCrystalMain.cfgNonDestructive) {
								world.setBlock(absX, absY, absZ, Blocks.air, 0,
										1 + 2);
								world.createExplosion(entity, absX, absY, absZ,
										1, false);
							}
						}
					}
				}
			} while (hit < ChaosCrystalMain.cfgHitsPerTick
					&& tries < ChaosCrystalMain.cfgMaxTriesPerTick);
		}

		if(allowItems) {
			if (hit < ChaosCrystalMain.cfgHitsPerTick) {
				List<EntityItem> items = new ArrayList<EntityItem>();

				for (Object obj : world.loadedEntityList) {
					if (obj instanceof EntityItem) {
						double el = ((EntityItem) obj).boundingBox
								.getAverageEdgeLength();
						double distX = ((EntityItem) obj).posX - posX;
						double distY = ((EntityItem) obj).posY - posY + el;
						double distZ = ((EntityItem) obj).posZ - posZ;
						double tmp_dist = distX * distX + distY * distY + distZ
								* distZ;
						if (Math.sqrt(tmp_dist) < range) {
							items.add((EntityItem) obj);
						}
					}
				}

				if (items.size() != 0) {

					EntityItem it = items.get(ChaosCrystalMain.rand.nextInt(items
							.size()));
					ItemStack is = it.getEntityItem();
					if (is != null) {
						List<Node> nodes;
						if (extract) {
							nodes = ChaosCrystalMain.degradationStore
									.getExtractionsFrom(is);
						} else {
							nodes = ChaosCrystalMain.degradationStore
									.getInfusionsFrom(is);
						}
						Node degradation = ListUtil.getRandomFromList(nodes,
								ChaosCrystalMain.rand);

						if (degradation != null) {
							Node[] parents;
							if (extract) {
								parents = degradation.getParents();
							} else {
								parents = new Node[] { degradation };
							}
							if (parents.length == 0) {
								// continue;
							} else {

								int[] aspects = degradation.getAspectDifference();

								if (!filterAspects.isEmpty()
										&& !filterAspects.containsAll(Arrays
												.asList(Aspects
														.getAspectNames(aspects)))) {
									// continue;
								} else {
									int count = 0;

									if (extract) {
										for (int st = 0; st < is.stackSize; st++) {
											if (!entity.canAcceptAspects(aspects)) {
												break;
											} else {
												count++;

												entity.addAspects(aspects);
											}
										}
									} else {
										for (int st = 0; st < is.stackSize; st++) {
											if (!entity.canProvideAspects(aspects)) {
												break;
											} else {
												count++;

												entity.subtractAspects(aspects);
											}
										}
									}
									hit += count;
									if (count > 0) {
										is.stackSize -= count;

										spawnMultiplesOfNodes(parents, count,
												world, it);

										if (is.stackSize == 0) {
											it.setDead();
										} else {
											it.setEntityItemStack(is);
										}

										CommonProxy.spawnParticleEffects(it,
												entity, 0);
										CommonProxy.spawnParticleEffects(it, 2);
									}
								}
							}
						} else {
							if (ChaosCrystalMain.cfgDebugOutput) {
								System.out.println(is.getDisplayName() + " - "
										+ is.getItem() + "/" + is.getItemDamage());
							}
							if (!ChaosCrystalMain.cfgNonDestructive) {
								it.setDead();
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
