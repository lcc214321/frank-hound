package com.hbfintech.hound.core.support;

import ch.qos.logback.classic.LoggerContext;
import com.hbfintech.hound.core.common.AsyncSimpleFuncThread;
import com.hbfintech.hound.core.event.HoundInitializedEvent;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

/**
 * Initialization entry of hound-core, instantiated by single lazy mode
 * {@link Sheepehound#hound}，that Avoid the destruction of a single case by
 * means of a private constructor and a synchronization method block.
 * Sheepehound is an implementation of the hound interface that provides
 * the ability to initialize the portal's ioc and register corresponding hound beans
 * {@link com.hbfintech.hound.core.support.HoundSheep}
 *
 * @author frank
 */
public class Sheepehound implements Hound
{
    private LoggerContext loggerContext = (LoggerContext) LoggerFactory
            .getILoggerFactory();

    private static Hound hound;

    private HoundEventNotifier houndEventNotifier;

    private SheepRegistry sheepRegistry;

    private SorterRegistry sorterRegistry;

    private BridgeRegistry bridgeRegistry;

    private BridgeAutowirer bridgeAutowirer;

    private HoundEventListenerRegistry houndEventListenerRegistry;

    private HoundAutoCloser autoCloser;

    private Sheepehound()
    {
        sheepRegistry = new SheepRegistry();
        sorterRegistry = new SorterRegistry();
        bridgeRegistry = new BridgeRegistry();
        bridgeAutowirer = new BridgeAutowirer();
        houndEventListenerRegistry = new HoundEventListenerRegistry();
        houndEventNotifier = new HoundEventNotifier(houndEventListenerRegistry);
        autoCloser = new HoundAutoCloser();
    }

    private void init()
    {
        //Init hound environment

        //Init hound sheep
        sheepRegistry.init();
        sorterRegistry.init();
        bridgeRegistry.init();
        bridgeAutowirer.init(bridgeRegistry);
        houndEventListenerRegistry.init();

        //Publish initialized event
        publishEvent(new HoundInitializedEvent(this));

        //Register closer which can release related resources at the end of the process automatically
        autoCloser.registerAutoCloser(sheepRegistry, sorterRegistry,
                bridgeRegistry, houndEventListenerRegistry);

        //Register close hook func
        Runtime.getRuntime()
                .addShutdownHook(new AsyncSimpleFuncThread(this::close));
    }

    @Override
    public <T> T getSheep(@NonNull String sheepName,
            @NonNull Class<T> sheepClazz)
    {
        SheepRegistry.HoundSheepGroup<T> basicContainer = sheepRegistry
                .getSheepGroup(sheepClazz);
        if (basicContainer != null)
        {
            return basicContainer.get(sheepName);
        }
        return null;
    }

    @Override
    public Object getBridge(@NonNull String bridgeName)
    {
        return bridgeRegistry.getBridge(bridgeName);
    }

    @Override
    public void publishEvent(@NonNull EventObject event)
    {
        houndEventNotifier.notify(event);
    }

    @Override
    public void sort()
    {
        sorterRegistry.getFirstSorter().sort();
    }

    public static synchronized Hound getHound()
    {
        if (null == hound)
        {
            hound = new Sheepehound();
            ((Sheepehound) hound).init();
        }
        return hound;
    }

    /**
     * Avoid being called by an external method in the form of privatization of the method
     */
    private void close()
    {
        autoCloser.exec();
    }
}
