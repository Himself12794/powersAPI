package com.himself12794.powersapi;

import com.himself12794.powersapi.spell.Dummy;
import com.himself12794.powersapi.spell.DummyHoming;
import com.himself12794.powersapi.spell.Flames;
import com.himself12794.powersapi.spell.Heal;
import com.himself12794.powersapi.spell.Immortalize;
import com.himself12794.powersapi.spell.Incinerate;
import com.himself12794.powersapi.spell.Lightning;
import com.himself12794.powersapi.spell.Push;
import com.himself12794.powersapi.spell.Slam;
import com.himself12794.powersapi.spell.Spell;
import com.himself12794.powersapi.spell.Telekinesis;

public class Spells {
	
	public static final Spell damage;
	public static final Incinerate incinerate;
	public static final Lightning lightning;
	public static final Heal heal;
	public static final Spell death;
	public static final Dummy dummy;
	public static final Immortalize immortalize;
	public static final Flames flames;
	public static final DummyHoming dummyHoming;
	public static final Slam slam;
	public static final Push push;
	public static final Telekinesis telekinesis;
	
	static {
		
		damage = Spell.lookupSpell("damage");
		death = Spell.lookupSpell("death");
		incinerate = (Incinerate) Spell.lookupSpell("incinerate");
		lightning = (Lightning) Spell.lookupSpell("lightning");
		heal = (Heal) Spell.lookupSpell("heal");
		dummy = (Dummy) Spell.lookupSpell("dummy");
		immortalize = (Immortalize) Spell.lookupSpell("immortalize");
		flames = (Flames) Spell.lookupSpell("flames");
		dummyHoming = (DummyHoming) Spell.lookupSpell("dummyHoming");
		slam = (Slam) Spell.lookupSpell("slam");
		push = (Push) Spell.lookupSpell("push");
		telekinesis = (Telekinesis) Spell.lookupSpell("telekinesis");
		
	}
}
