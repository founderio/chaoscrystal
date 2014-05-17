package founderio.chaoscrystal.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceSentrySnowball extends EntityDamageSource {

	 private Entity indirectEntity;

	    public DamageSourceSentrySnowball(String par1Str, Entity par2Entity, Entity par3Entity)
	    {
	        super(par1Str, par2Entity);
	        this.indirectEntity = par3Entity;
	        setProjectile();
	        setDamageBypassesArmor();
	    }

	    public Entity getSourceOfDamage()
	    {
	        return this.damageSourceEntity;
	    }

	    public Entity getEntity()
	    {
	        return this.indirectEntity;
	    }

	    @Override
	    public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
	    {
	        IChatComponent ichatcomponent = this.indirectEntity == null ? this.damageSourceEntity.func_145748_c_() : this.indirectEntity.func_145748_c_();
	        ItemStack itemstack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
	        String s = "death.attack." + this.damageType;
	        String s1 = s + ".item";
	        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] {p_151519_1_.func_145748_c_(), ichatcomponent, itemstack.func_151000_E()}): new ChatComponentTranslation(s, new Object[] {p_151519_1_.func_145748_c_(), ichatcomponent});
	    }

}
