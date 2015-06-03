package com.himself12794.powersapi;

import com.himself12794.powersapi.api.power.Power;
import com.himself12794.powersapi.api.power.PowerInstant;
import com.himself12794.powersapi.power.Dummy;
import com.himself12794.powersapi.power.DummyHoming;
import com.himself12794.powersapi.power.Flames;
import com.himself12794.powersapi.power.Heal;
import com.himself12794.powersapi.power.Immortalize;
import com.himself12794.powersapi.power.Incinerate;
import com.himself12794.powersapi.power.Lightning;
import com.himself12794.powersapi.power.Push;
import com.himself12794.powersapi.power.Slam;
import com.himself12794.powersapi.power.Telekinesis;

public class Powers {
	
	public static final Power damage;
	public static final Incinerate incinerate;
	public static final Lightning lightning;
	public static final Heal heal;
	public static final Power death;
	public static final Dummy dummy;
	public static final Immortalize immortalize;
	public static final Flames flames;
	public static final DummyHoming dummyHoming;
	public static final Slam slam;
	public static final Push push;
	public static final Telekinesis telekinesis;
	
	static {

		damage = Power.registerPower(new PowerInstant().setUnlocalizedName("damage"));	
		death = Power.registerPower(new PowerInstant().setUnlocalizedName("death").setPower(1000.0F).setCoolDown(178));	
		incinerate = (Incinerate) Power.registerPower(new Incinerate());
		lightning = (Lightning) Power.registerPower(new Lightning());
		heal = (Heal) Power.registerPower(new Heal());
		dummy = (Dummy) Power.registerPower(new Dummy());
		immortalize = (Immortalize) Power.registerPower(new Immortalize());
		flames = (Flames) Power.registerPower(new Flames());
		dummyHoming = (DummyHoming) Power.registerPower(new DummyHoming());
		slam = (Slam) Power.registerPower(new Slam());
		push = (Push) Power.registerPower(new Push());
		telekinesis = (Telekinesis) Power.registerPower(new Telekinesis());
		
	}
}
