package com.hbfintech.hound.core.support;

import ch.qos.logback.classic.LoggerContext;
import com.hbfintech.hound.core.acceptor.sorter.SorterInitializer;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

/**
 * 初始化并持有容器等重要实例的上下文，作用类似于ApplicationContext
 * @author frank
 */
public class HoundShepherd implements HoundContext
{
    private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    private static HoundContext shepherd;

    private HoundComponentRegistry componentRegistry;

    private

    private SorterInitializer sorterLoader;

    private HoundBridgeRegistry houndBridgeRegistry;

    private HoundBridgeAutowirer houndBridgeAutowirer;

    private HoundShepherd()
    {
        componentRegistry = new HoundComponentRegistry();
        sorterLoader= new SorterInitializer();
        houndBridgeRegistry = new HoundBridgeRegistry();
        houndBridgeAutowirer = new HoundBridgeAutowirer(houndBridgeRegistry);
    }

    public void init()
    {
        componentRegistry.init();
        sorterLoader.init();
        houndBridgeRegistry.init();
        houndBridgeAutowirer.init();
    }

    @Override
    public <T> T getComponent(@NonNull String componentName,@NonNull Class<T> componentClazz)
    {
        HoundComponentRegistry.HoundComponentGroup<T> basicContainer = componentRegistry
                .getComponentsGroup(componentClazz);
        if(basicContainer!=null)
        {
            return basicContainer.get(componentName);
        }
        return null;
    }

    @Override
    public Object getBridge(@NonNull String bridgeName)
    {
        return houndBridgeRegistry.getBridge(bridgeName);
    }

    public static HoundContext getContext()
    {
        if(null == shepherd)
        {
            shepherd = new HoundShepherd();
            ((HoundShepherd)shepherd).init();
        }
        return shepherd;
    }

    @Override
    public void sort()
    {
        //TODO： 待优化，准备使用单线程事件驱动模型，正在斟酌如何实现
        sorterLoader.getFirstSorter().sort();
    }
}
