package fr.karmaowner.companies.pluginevent;

import fr.karmaowner.data.CompanyData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CompanyGainXpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public final CompanyData company;

    public final Player xpOwner;

    public double xpGain;


    private boolean isCancelled = false;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public CompanyGainXpEvent(Player player, CompanyData company, double xpGain)
    {
        super(false);
        this.xpOwner = player;
        this.company = company;
        this.xpGain = xpGain;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }


}
