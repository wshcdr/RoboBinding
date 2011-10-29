/**
 * PropertiesImpl.java
 * Oct 27, 2011 Copyright Cheng Wei and Robert Taylor
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package robobinding.property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import robobinding.DependsOn;
import robobinding.ItemPresentationModel;
import robobinding.itempresentationmodel.TypedCursor;

import com.google.common.collect.Maps;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
class PropertyCreator
{
	private final Object bean;
	private Map<String, PropertyDescriptor> availableProperties;
	public PropertyCreator(Object bean)
	{
		this.bean = bean;
		initializeAvailablePropertyNames();
	}
	private void initializeAvailablePropertyNames()
	{
		availableProperties = Maps.newHashMap();
		try
		{
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) 
			{
				availableProperties.put(propertyDescriptor.getName(), propertyDescriptor);
			}
		} catch (IntrospectionException e)
		{
			throw new RuntimeException(e);
		}
	}
	public <T> AbstractProperty<T> createProperty(String propertyName)
	{
		PropertyAccessor<T> propertyAccessor = getPropertyAccessor(propertyName);
		AbstractProperty<T> property = null;
		if(propertyAccessor.hasAnnotation(DependsOn.class))
		{
			property = new DependencyProperty<T>(bean, propertyAccessor, availableProperties.keySet());
		}else
		{
			property = new SimpleProperty<T>(bean, propertyAccessor);
		}
		return property;
	}
	public <T> AbstractDataSetProperty<T> createDataSetProperty(String propertyName)
	{
		PropertyAccessor<Object> propertyAccessor = getPropertyAccessor(propertyName);
		if(propertyAccessor.hasAnnotation(ItemPresentationModel.class))
		{
			if(List.class.isAssignableFrom(propertyAccessor.getPropertyType()))
			{
				return new ListDataSetProperty<T>(bean, propertyAccessor);
			}else if(TypedCursor.class.isAssignableFrom(propertyAccessor.getPropertyType()))
			{
				return new CursorDataSetProperty<T>(bean, propertyAccessor);
			}else
			{
				throw new RuntimeException("The property '"+describeProperty(propertyName)+"' has an Unsupported dataset type '"+propertyAccessor.getPropertyType()+"'");
			}
		}else
		{
			throw new RuntimeException("The property '"+describeProperty(propertyName)+"' that provides dataset is missing annotation @ItemPresentation configured");
		}
	}
	private <T> PropertyAccessor<T> getPropertyAccessor(String propertyName)
	{
		PropertyDescriptor propertyDescriptor = availableProperties.get(propertyName);
		if(propertyDescriptor == null)
		{
			throw new RuntimeException("The property '"+describeProperty(propertyName)+"' does not exist");
		}
		return new PropertyAccessor<T>(propertyDescriptor, bean.getClass());
	}
	private String describeProperty(String propertyName)
	{
		String beanClassName = bean.getClass().getName();
		return beanClassName+"."+propertyName;
	}
}