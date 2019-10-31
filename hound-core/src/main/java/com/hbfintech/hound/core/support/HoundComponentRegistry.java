package com.hbfintech.hound.core.support;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 二级映射容器
 */
public class HoundComponentRegistry
{
    private Map<Class, HoundComponent> componentsMapper = new HashMap<>();

    public HoundComponentRegistry()
    {
        initContainer();
    }

    private void initContainer()
    {
        //扫描方法区下的类信息
        Reflections reflections = new Reflections("com");
        Set<Class<?>> componentClazzSet = reflections.getTypesAnnotatedWith(
                com.hbfintech.hound.core.support.HoundComponent.class);

        parseComponent(componentClazzSet);

    }

    private void parseComponent(Set<Class<?>> componentClazzSet)
    {



    }

    public <T> HoundComponent<T> getCompontsContainer(Class<T> clazz)
    {
        return componentsMapper.get(clazz);
    }

    //    public static void main(String[] args)
//    {
//        HoundComponentContainer container = new HoundComponentContainer();
//        Unpack mvcUnpack = container.getCompontsContainer(Unpack.class).get("mvc");
//
//
//    }

    public class HoundComponent<T>
    {
        private Map<String,T> componentInstanceMapper = new HashMap<>();

        public HoundComponent()
        {
        }

        public void add(String componentName, T componentInstance)
        {
            componentInstanceMapper.put(componentName,componentInstance);
        }

        public T get(String componentName)
        {
            return componentInstanceMapper.get(componentName);
        }
    }

}